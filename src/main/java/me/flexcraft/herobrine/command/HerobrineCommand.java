package me.flexcraft.herobrine.command;

import me.flexcraft.herobrine.HerobrinePlugin;
import me.flexcraft.herobrine.fake.FakeHerobrineSpawner;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HerobrineCommand implements CommandExecutor {

    private final HerobrinePlugin plugin;

    public HerobrineCommand(HerobrinePlugin plugin) {
        this.plugin = plugin;
    }

    private String msg(String path) {
        return plugin.getConfig().getString(
                "messages." + path,
                "§cСообщение не найдено в config.yml"
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("herobrine.use")) {
            sender.sendMessage(msg("no-permission"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(msg("usage"));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(msg("player-not-found"));
            return true;
        }

        sender.sendMessage(msg("admin-trigger").replace("{player}", target.getName()));
        target.sendMessage(msg("target-message"));

        // 👻 ХОРРОР-ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1));

        // 🔥 СПАВН ХЕРОБРИНА ПРЯМО ПЕРЕД ИГРОКОМ
        FakeHerobrineSpawner.spawn(target);

        return true;
    }
}
