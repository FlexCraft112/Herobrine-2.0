package me.flexcraft.herobrine.command;

import me.flexcraft.herobrine.HerobrinePlugin;
import me.flexcraft.herobrine.npc.HerobrineNPCSpawner;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HerobrineCommand implements CommandExecutor {

    private final HerobrinePlugin plugin;

    public HerobrineCommand(HerobrinePlugin plugin) {
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

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден");
            return true;
        }

        sender.sendMessage("§7Вы призвали §fHerobrine §7для §c" + target.getName());
        target.sendMessage("§8Вы чувствуете §fчужой взгляд...");

        HerobrineNPCSpawner.spawn(plugin, target);
        return true;
    }
}
