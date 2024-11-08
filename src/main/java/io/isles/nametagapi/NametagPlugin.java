package io.isles.nametagapi;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public final class NametagPlugin extends JavaPlugin {
    @Getter
    private static NametagPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
    }
}