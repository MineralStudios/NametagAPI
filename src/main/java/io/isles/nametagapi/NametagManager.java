package io.isles.nametagapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import gg.mineral.api.nametag.NametagGroup;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * This class dynamically creates teams with numerical names and certain
 * prefixes/suffixes (it ignores teams with other characters) to assign unique
 * prefixes and suffixes to specific players in the game. This class makes edits
 * to the <b>scoreboard.dat</b> file, adding and removing teams on the fly.
 * 
 * @author Levi Webb (Original)
 * @author Hyphenical Technologies (Modifiers)
 */
@RequiredArgsConstructor
public final class NametagManager {

    /** Prefix to append to all team names. */
    private static final String TEAM_NAME_PREFIX = "NTP";
    private final NametagGroup group;
    private Map<TeamInfo, List<String>> teams = new Object2ObjectOpenHashMap<>();
    private IntList list = new IntArrayList();
    private Plugin plugin;

    /**
     * Initializes this class and loads current teams that are manipulated by
     * this plugin.
     */
    public void load() {
        plugin = NametagPlugin.getInstance();

        for (val teamInfo : getTeams()) {
            int entry = -1;

            try {
                entry = Integer.parseInt(teamInfo.getName());
            } catch (Exception exc) {
                plugin.getLogger().log(Level.FINEST, "Failed to parse integer: " + teamInfo.getName());
            }

            if (entry != -1)
                list.add(entry);
        }
    }

    boolean isManaged(String player) {
        for (val entry : teams.entrySet())
            for (val listedPlayer : entry.getValue())
                if (listedPlayer.equalsIgnoreCase(player))
                    return true;

        return false;
    }

    /**
     * Updates a player's prefix and suffix in the scoreboard and above their
     * head.
     * 
     * <br>
     * <br>
     * 
     * If either the prefix or suffix is null or empty, it will be replaced with
     * the current prefix/suffix
     * 
     * @param player The specified player.
     * @param prefix The prefix to set for the given player.
     * @param suffix The suffix to set for the given player.
     */
    void update(String player, String prefix, String suffix) {
        if (prefix == null || prefix.isEmpty())
            prefix = getPrefix(player);

        if (suffix == null || suffix.isEmpty())
            suffix = getSuffix(player);

        val teamInfo = getTeamInfo(prefix, suffix);

        addToTeam(teamInfo, player);
    }

    /**
     * Updates a player's prefix and suffix in the scoreboard and above their
     * head.
     * 
     * <br>
     * <br>
     * 
     * If either the prefix or suffix is null or empty, it will be removed from
     * the player's nametag.
     * 
     * @param player The specified player.
     * @param prefix The prefix to set for the given player.
     * @param suffix The suffix to set for the given player.
     */
    void overlap(String player, String prefix, String suffix) {
        if (prefix == null)
            prefix = "";

        if (suffix == null)
            suffix = "";

        val t = getTeamInfo(prefix, suffix);

        addToTeam(t, player);
    }

    /**
     * Clears a player's nametag.
     * 
     * @param player The specified player.
     */
    public void clear(String player) {
        removeFromTeam(player);
    }

    /**
     * Retrieves a player's prefix
     * 
     * @param player The specified player.
     * @return The player's prefix.
     */
    String getPrefix(String player) {
        for (val team : getTeams())
            for (val p : getTeamPlayers(team))
                if (p.equals(player))
                    return team.getPrefix();

        return "";
    }

    /**
     * Retrieves a player's suffix
     * 
     * @param player The specified player.
     * @return The player's suffix.
     */
    String getSuffix(String player) {
        for (val team : getTeams())
            for (val p : getTeamPlayers(team))
                if (p.equals(player))
                    return team.getSuffix();

        return "";
    }

    /**
     * Retrieves the player's entire name with both the prefix and suffix.
     * 
     * @param player The specified player.
     * @return The entire nametag.
     */
    String getFormattedName(String player) {
        return getPrefix(player) + player + getSuffix(player);
    }

    /**
     * Sends the current team setup and their players to the given player. This
     * should be called when players join the server.
     * 
     * @param player The player to send the packets to.
     */
    public void sendTeamsToPlayer(Player player) {
        try {
            for (val team : getTeams()) {
                var packet = new PacketHandler(team.getName(), team.getPrefix(), team.getSuffix(),
                        new ArrayList<String>(), 0);
                packet.sendToPlayer(player);
                packet = new PacketHandler(team.getName(), Arrays.asList(getTeamPlayers(team)), 3);
                packet.sendToPlayer(player);
            }
        } catch (Exception exc) {
            plugin.getLogger().warning("Failed to send packet for player (Packet209SetScoreboardTeam): ");
            exc.printStackTrace();
        }
    }

    /**
     * Clears out all teams and removes them for all the players. Called when
     * the plugin is disabled.
     */
    public void reset() {
        for (val team : getTeams())
            removeTeam(team);
    }

    /**
     * Declares a new team in the scoreboard.dat of the given main world.
     * 
     * @param name   The team name.
     * @param prefix The team's prefix.
     * @param suffix The team's suffix.
     * @return The created TeamInfo.
     */
    private TeamInfo declareTeam(String name, String prefix, String suffix) {
        if (getTeam(name) != null) {
            val team = getTeam(name);
            removeTeam(team);
        }

        val team = new TeamInfo(name);

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        register(team);

        return team;
    }

    /**
     * Gets the ScoreboardTeam for the given prefix and suffix, and if none
     * matches, creates a new team with the provided info. This also removes
     * teams that currently have no players.
     * 
     * @param prefix The team's prefix.
     * @param suffix The team's suffix.
     * @return A team with the corresponding prefix/suffix.
     */
    private TeamInfo getTeamInfo(String prefix, String suffix) {
        update();

        for (int t : list) {
            if (getTeam(TEAM_NAME_PREFIX + t) != null) {
                val team = getTeam(TEAM_NAME_PREFIX + t);

                if (team != null && team.getSuffix().equals(suffix) && team.getPrefix().equals(prefix))
                    return team;
            }
        }

        return declareTeam(TEAM_NAME_PREFIX + nextName(), prefix, suffix);
    }

    /**
     * Returns the next available team name that is not taken.
     * 
     * @return an integer that for a team name that is not taken.
     */
    private int nextName() {
        int at = 0;
        boolean cont = true;

        while (cont) {
            cont = false;

            for (int t : list) {
                if (t == at) {
                    at++;
                    cont = true;
                }
            }
        }

        list.add(at);
        return at;
    }

    /**
     * Removes any teams that do not have any players in them.
     */
    private void update() {
        for (val team : getTeams()) {
            int entry = -1;

            try {
                entry = Integer.parseInt(team.getName());
            } catch (Exception exc) {
            }

            if (entry != -1) {
                if (getTeamPlayers(team).length == 0) {
                    removeTeam(team);
                    list.removeInt(entry);
                }
            }
        }
    }

    /**
     * Sends packets out to players to add the given team
     * 
     * @param team the team to add
     */
    private void sendPacketsAddTeam(TeamInfo team) {
        val players = group.getPlayers();
        try {
            for (val p : players) {
                val mod = new PacketHandler(team.getName(), team.getPrefix(), team.getSuffix(),
                        new ArrayList<String>(), 0);
                mod.sendToPlayer(p);
            }
        } catch (Exception exc) {
            plugin.getLogger().warning("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            exc.printStackTrace();
        }
    }

    /**
     * Sends packets out to players to remove the given team
     * 
     * @param team the team to remove
     */
    private void sendPacketsRemoveTeam(TeamInfo team) {
        boolean cont = false;

        for (val t : getTeams())
            if (t == team)
                cont = true;

        if (!cont)
            return;

        val players = group.getPlayers();
        try {
            for (val p : players) {
                val mod = new PacketHandler(team.getName(), team.getPrefix(), team.getSuffix(),
                        new ArrayList<String>(), 1);
                mod.sendToPlayer(p);
            }
        } catch (Exception exc) {
            plugin.getLogger().warning("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            exc.printStackTrace();
        }
    }

    /**
     * Sends out packets to players to add the given player to the given team
     * 
     * @param team   - The team to use
     * @param player - The player to add
     */
    private void sendPacketsAddToTeam(TeamInfo team, String player) {
        boolean cont = false;

        for (val t : getTeams())
            if (t == team)
                cont = true;

        if (!cont)
            return;

        val players = group.getPlayers();
        try {
            for (val p : players) {
                PacketHandler packet = new PacketHandler(team.getName(), Arrays.asList(player), 3);
                packet.sendToPlayer(p);
            }
        } catch (Exception exc) {
            plugin.getLogger().warning("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            exc.printStackTrace();
        }
    }

    /**
     * Sends out packets to players to remove the given player from the given
     * team.
     * 
     * @param team   - The team to remove from
     * @param player - The player to remove
     */
    private void sendPacketsRemoveFromTeam(TeamInfo team, String player) {
        boolean cont = false;

        for (val t : getTeams())
            if (t == team)
                for (val p : getTeamPlayers(t))
                    if (p.equals(player))
                        cont = true;

        if (!cont)
            return;

        val players = group.getPlayers();
        try {
            for (val p : players) {
                val packet = new PacketHandler(team.getName(), Arrays.asList(player), 4);
                packet.sendToPlayer(p);
            }
        } catch (Exception exc) {
            plugin.getLogger().warning("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            exc.printStackTrace();
        }
    }

    private void addToTeam(TeamInfo team, String player) {
        removeFromTeam(player);
        val list = teams.get(team);

        if (list != null) {
            list.add(player);

            val p = Bukkit.getPlayerExact(player);

            if (p != null)
                sendPacketsAddToTeam(team, p.getName());
            else {
                @SuppressWarnings("deprecation")
                val p2 = Bukkit.getOfflinePlayer(player);
                sendPacketsAddToTeam(team, p2.getName());
            }
        }
    }

    private void register(TeamInfo team) {
        teams.put(team, new ArrayList<String>());
        sendPacketsAddTeam(team);
    }

    private void removeTeam(TeamInfo team) {
        sendPacketsRemoveTeam(team);
        teams.remove(team);
    }

    @Nullable
    private TeamInfo removeFromTeam(String player) {
        for (val team : teams.keySet()) {
            val list = teams.get(team);

            for (val p : list) {
                if (p.equals(player)) {
                    val pl = Bukkit.getPlayerExact(player);

                    if (pl != null)
                        sendPacketsRemoveFromTeam(team, pl.getName());
                    else {
                        @SuppressWarnings("deprecation")
                        val p2 = Bukkit.getOfflinePlayer(p);
                        sendPacketsRemoveFromTeam(team, p2.getName());
                    }

                    list.remove(p);

                    return team;
                }
            }
        }

        return null;
    }

    @Nullable
    private TeamInfo getTeam(String name) {
        for (val team : teams.keySet().toArray(new TeamInfo[teams.size()]))
            if (team.getName().equals(name))
                return team;

        return null;
    }

    private TeamInfo[] getTeams() {
        val list = new TeamInfo[teams.size()];
        int at = 0;

        for (val team : teams.keySet()) {
            list[at] = team;
            at++;
        }

        return list;
    }

    private String[] getTeamPlayers(TeamInfo team) {
        val list = teams.get(team);
        return list != null ? list.toArray(new String[list.size()]) : new String[0];
    }

}