package me.flexcraft.herobrine;

import org.bukkit.plugin.java.JavaPlugin;

public final class HerobrinePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Herobrine-2.0 has awakened...");
    }

    @Override
    public void onDisable() {
        getLogger().info("Herobrine-2.0 has vanished.");
    }
}
