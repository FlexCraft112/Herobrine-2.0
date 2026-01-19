package me.flexcraft.herobrine;

import me.flexcraft.herobrine.command.HerobrineCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class HerobrinePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("herobrine").setExecutor(new HerobrineCommand(this));
        getLogger().info("Herobrine-2.0 активирован.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Herobrine-2.0 отключён.");
    }
}
