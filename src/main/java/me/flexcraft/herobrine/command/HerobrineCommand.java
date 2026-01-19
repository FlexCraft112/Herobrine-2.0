package me.flexcraft.herobrine.command;

import me.flexcraft.herobrine.HerobrinePlugin;
import me.flexcraft.herobrine.fake.FakeHerobrineSpawner;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

        sender.sendMessage(
                msg("admin-trigger").replace("{player}", target.getName())
        );
        target.sendMessage(msg("target-message"));

        // 👁️ СПАВН ХЕРОБРИНА
        FakeHerobrineSpawner.spawn(plugin, target);

        // 😨 ХОРРОР-ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SILENCE, 100, 0));

        // 🔊 ЖУТКИЕ ЗВУКИ
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1.5f, 0.6f);
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0f, 0.5f);

        return true;
    }
}
