package io.isles.nametagapi;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.val;

public final class NametagPlugin extends JavaPlugin implements Listener {

    private static NametagPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        NametagManager.load();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        NametagManager.reset();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();
        NametagManager.sendTeamsToPlayer(player);
        NametagManager.clear(player.getName());
    }

    static NametagPlugin getInstance() {
        return instance;
    }

}