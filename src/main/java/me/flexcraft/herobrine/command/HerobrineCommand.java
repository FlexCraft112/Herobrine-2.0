package me.flexcraft.herobrine.command;

import me.flexcraft.herobrine.npc.HerobrineNPCSpawner;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HerobrineCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public HerobrineCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("herobrine.use")) {
            sender.sendMessage("§cНет прав");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cИспользование: /herobrine <ник>");
            return true;
        }

        if (HerobrineNPCSpawner.isActive()) {
            sender.sendMessage("§cХеробрин уже рядом...");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден");
            return true;
        }

        sender.sendMessage("§7Ты призвал §4нечто§7...");
        HerobrineNPCSpawner.spawn(plugin, target);
        return true;
    }
}
