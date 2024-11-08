package io.isles.nametagapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import gg.mineral.api.nametag.NametagGroup;
import io.isles.nametagapi.NametagChangeEvent.NametagChangeReason;
import io.isles.nametagapi.NametagChangeEvent.NametagChangeType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;

/**
 * This API class is used to set prefixes and suffixes at a high level. These
 * methods fire events, which can be listened to, and cancelled.
 * 
 * It is recommended to use this class for light use of NametagAPI.
 * 
 * @author Levi Webb (Original)
 * @author Hyphenical Technologies (Modifiers)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NametagAPI {

    private static Plugin plugin;

    static {
        plugin = NametagPlugin.getInstance();
    }

    /**
     * Sets the custom prefix for the given player <br>
     * <br>
     * This method schedules a task with the request to change the player's name
     * to prevent it from clashing with the PlayerJoinEvent in NametagAPI.
     * 
     * @param group  The group to set the prefix for.
     * @param player The player to set the prefix for.
     * @param prefix The prefix to use.
     */
    public static void setPrefix(final NametagGroup group, final String player, final String prefix) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            val event = new NametagChangeEvent(player, getPrefix(group, player), getSuffix(group, player), prefix, "",
                    NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
                group.getManager().update(player, prefix, "");
        });
    }

    /**
     * Sets the custom suffix for the given player.
     * 
     * @param group  The group to set the suffix for.
     * @param player The player to set the suffix for.
     * @param suffix The suffix to use.
     */
    public static void setSuffix(final NametagGroup group, final String player, final String suffix) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            val event = new NametagChangeEvent(player, getPrefix(group, player), getSuffix(group, player), "", suffix,
                    NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
                group.getManager().update(player, "", suffix);
        });
    }

    /**
     * Sets the custom given prefix and suffix to the player, overwriting any
     * existing prefix or suffix. If a given prefix or suffix is null/empty, it
     * will be removed from the player.
     * 
     * @param group  The group to set the prefix and suffix for.
     * @param player The player to set the prefix and suffix for.
     * @param prefix The prefix to use.
     * @param suffix The suffix to use.
     */
    public static void setNametagHard(final NametagGroup group, final String player, final String prefix,
            final String suffix) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            val event = new NametagChangeEvent(player, getPrefix(group, player), getSuffix(group, player), prefix,
                    suffix,
                    NametagChangeType.HARD, NametagChangeReason.CUSTOM);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
                group.getManager().overlap(player, prefix, suffix);
        });
    }

    /**
     * Sets the custom given prefix and suffix to the player. If a given prefix
     * or suffix is empty/null, it will be ignored. <br>
     * <br>
     * 
     * @param group  The group to set the prefix and suffix for.
     * @param player The player to set the prefix and suffix for.
     * @param prefix The prefix to use.
     * @param suffix The suffix to use.
     */
    public static void setNametagSoft(final NametagGroup group, final String player, final String prefix,
            final String suffix) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            val event = new NametagChangeEvent(player, getPrefix(group, player), getSuffix(group, player), prefix,
                    suffix,
                    NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
                group.getManager().update(player, prefix, suffix);
        });
    }

    /**
     * Sets the custom given prefix and suffix to the player, overwriting any
     * existing prefix or suffix. If a given prefix or suffix is null/empty, it
     * will be removed from the player.
     * 
     * <br>
     * <br>
     * 
     * This method does not save the modified nametag, it only updates it about
     * their head. use setNametagSoft and setNametagHard if you don't know what
     * you're doing.
     * 
     * @param group  The group to set the prefix and suffix for.
     * @param player The player to set the prefix and suffix for.
     * @param prefix The prefix to use.
     * @param suffix The suffix to use.
     */
    public static void updateNametagHard(final NametagGroup group, final String player, final String prefix,
            final String suffix) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            val event = new NametagChangeEvent(player, getPrefix(group, player), getSuffix(group, player), prefix,
                    suffix,
                    NametagChangeType.HARD, NametagChangeReason.CUSTOM);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
                group.getManager().overlap(player, prefix, suffix);
        });
    }

    /**
     * Sets the custom given prefix and suffix to the player. If a given prefix
     * or suffix is empty/null, it will be ignored.
     * 
     * <br>
     * <br>
     * 
     * This method does not save the modified nametag, it only updates it about
     * their head. use setNametagSoft and setNametagHard if you don't know what
     * you're doing.
     * 
     * <br>
     * <br>
     * 
     * This method schedules a task with the request to change the player's name
     * to prevent it from clashing with the PlayerJoinEvent in NametagAPI.
     * 
     * @param group  The group to set the prefix and suffix for.
     * @param player The player to set the prefix and suffix for.
     * @param prefix The prefix to use.
     * @param suffix The suffix to use.
     */
    public static void updateNametagSoft(final NametagGroup group, final String player, final String prefix,
            final String suffix) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            val event = new NametagChangeEvent(player, getPrefix(group, player), getSuffix(group, player), prefix,
                    suffix,
                    NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled())
                group.getManager().update(player, prefix, suffix);
        });
    }

    /**
     * Clears the given player's custom prefix and suffix and sets it to the
     * group node that applies to that player. <br>
     * <br>
     * This method schedules a task with the request to change the player's name
     * to prevent it from clashing with the PlayerJoinEvent in NametagAPI.
     * 
     * @param group  The group to reset the nametag for.
     * @param player The player to reset.
     */
    public static void resetNametag(final NametagGroup group, final String player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> group.getManager().clear(player));
    }

    /**
     * Returns the prefix for the given player name
     * 
     * @param group  The group to get the prefix for.
     * @param player The player to check
     * @return the player's prefix, or null if there is none.
     */
    public static String getPrefix(final NametagGroup group, String player) {
        return group.getManager().getPrefix(player);
    }

    /**
     * Returns the suffix for the given player name
     * 
     * @param group  The group to get the suffix for.
     * @param player The player to check.
     * @return The player's suffix, or null if there is none.
     */
    public static String getSuffix(final NametagGroup group, String player) {
        return group.getManager().getSuffix(player);
    }

    /**
     * Returns the entire nametag for the given player
     * 
     * @param group  The group to get the nametag for.
     * @param player The player to check
     * @return The player's prefix, actual name, and suffix in one string
     */
    public static String getNametag(final NametagGroup group, String player) {
        return group.getManager().getFormattedName(player);
    }

    /**
     * Returns whether the player currently has a custom nametag applied.
     * 
     * @param group  The group to check the nametag for.
     * @param player The player to check.
     * @return {@code true} if there is a custom nametag set, otherwise
     *         {@code false}.
     */
    public static boolean hasCustomNametag(final NametagGroup group, String player) {
        return group.getManager().isManaged(player);
    }

}