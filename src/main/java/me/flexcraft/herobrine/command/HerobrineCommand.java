package me.flexcraft.herobrine.command;

import me.flexcraft.herobrine.HerobrinePlugin;
import me.flexcraft.herobrine.fake.FakeHerobrineSpawner;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class HerobrineCommand implements CommandExecutor {

    private final HerobrinePlugin plugin;

    public HerobrineCommand(HerobrinePlugin plugin) {
        this.plugin = plugin;
    }

    private String msg(String path) {
        return plugin.getConfig().getString("messages." + path, "§cСообщение не найдено");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("herobrine.use")) {
            sender.sendMessage(msg("no-permission"));
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

        sender.sendMessage("§7Вы призвали §cХеробрина §7для §f" + target.getName());
        target.sendMessage("§8Вы чувствуете §fчужой взгляд...");

        FakeHerobrineSpawner.spawn(plugin, target);
        return true;
    }
}
