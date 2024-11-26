package gg.mineral.api.nametag;

import java.util.Collection;

import org.bukkit.entity.Player;

import io.isles.nametagapi.NametagManager;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.val;

public class NametagGroup {
    @Getter
    private final NametagManager manager;
    @Getter
    private final Collection<Player> players = new ObjectOpenHashSet<>();
    private boolean deleted = false;

    public NametagGroup(Player... players) {
        this.manager = new NametagManager(this);
        this.manager.load();
        this.add(players);
    }

    public void add(Player... players) {
        if (deleted)
            throw new IllegalStateException("This group has been deleted.");

        for (val player : players) {
            this.players.add(player);
            manager.sendTeamsToPlayer(player);
            manager.clear(player.getName());
        }
    }

    public void remove(Player player) {
        if (deleted)
            throw new IllegalStateException("This group has been deleted.");

        manager.clear(player.getName());
        players.remove(player);
    }

    public void delete() {
        if (deleted)
            throw new IllegalStateException("This group has been deleted.");

        manager.reset();
        deleted = true;
    }
}
