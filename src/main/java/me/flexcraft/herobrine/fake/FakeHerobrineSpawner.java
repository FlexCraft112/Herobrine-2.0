package me.flexcraft.herobrine.fake;

import me.flexcraft.herobrine.HerobrinePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class FakeHerobrineSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        // 📍 ПОЯВЛЯЕТСЯ ПРЯМО ПЕРЕД ИГРОКОМ (БЛИЗКО)
        Location base = target.getLocation();
        Vector forward = base.getDirection().normalize().multiply(2);
        Location spawnLoc = base.clone().add(forward);
        spawnLoc.setY(base.getY());

        // 👤 ХЕРОБРИН
        Villager npc = target.getWorld().spawn(spawnLoc, Villager.class, v -> {
            v.setAI(false);
            v.setSilent(true);
            v.setInvulnerable(true);
            v.setCollidable(false);
            v.setCustomName("§5Herobrine");
            v.setCustomNameVisible(true);
        });

        // 👁️ СРАЗУ СМОТРИТ В ЛИЦО
        lookAt(npc, target);

        // 😨 МЯГКИЙ ХОРРОР (не ослепление)
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 0));

        // 🔊 ТИХИЙ ЗВУК (НЕ СКРИМЕР)
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_BREATH, 1.0f, 0.6f);

        // 👁️ МЕДЛЕННО СЛЕДИТ ВЗГЛЯДОМ
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!npc.isValid() || !target.isOnline()) {
                task.cancel();
                return;
            }
            lookAt(npc, target);
        }, 0L, 5L);

        // ⏳ СТОИТ И СМОТРИТ ~4 СЕК
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            // 🌫️ ПОСЛЕДНИЙ ЭФФЕКТ
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
            target.playSound(target.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.7f, 0.4f);

            if (npc.isValid()) {
                npc.remove();
            }

        }, 80L);
    }

    private static void lookAt(Villager npc, Player target) {
        Location npcLoc = npc.getLocation();
        Vector dir = target.getEyeLocation().toVector().subtract(npcLoc.toVector());
        npcLoc.setDirection(dir);
        npc.teleport(npcLoc);
    }
}
