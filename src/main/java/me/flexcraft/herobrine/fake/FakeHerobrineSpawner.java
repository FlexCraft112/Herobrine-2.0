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

        // 📍 ПОЯВЛЯЕТСЯ ОЧЕНЬ БЛИЗКО ПЕРЕД ИГРОКОМ
        Location base = target.getLocation();
        Vector forward = base.getDirection().normalize().multiply(1.8);
        Location spawnLoc = base.clone().add(forward);
        spawnLoc.setY(base.getY());

        // 👤 ХЕРОБРИН (БЕЗ ИМЕНИ)
        Villager npc = target.getWorld().spawn(spawnLoc, Villager.class, v -> {
            v.setAI(false);
            v.setSilent(true);
            v.setInvulnerable(true);
            v.setCollidable(false);
            v.setCustomNameVisible(false);
            v.setRemoveWhenFarAway(false);
        });

        // 👁️ СРАЗУ СМОТРИТ В ГЛАЗА
        lookAt(npc, target);

        // 🌑 МИР ТЕМНЕЕТ, ОН — НЕТ
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));

        // 👁️ «БЕЛЫЕ ГЛАЗА» — СВЕТЯЩИЙСЯ СИЛУЭТ
        npc.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
        npc.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));

        // 🔊 ТИХОЕ ДЫХАНИЕ (ОЧЕНЬ НЕПРИЯТНО В НАУШНИКАХ)
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_BREATH, 1.0f, 0.5f);

        // 👁️ МЕДЛЕННЫЙ, НЕЕСТЕСТВЕННЫЙ ВЗГЛЯД
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!npc.isValid() || !target.isOnline()) {
                task.cancel();
                return;
            }
            lookAt(npc, target);
        }, 0L, 6L);

        // ⏳ СМОТРИТ 4 СЕКУНДЫ
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            // 🕳️ ПОСЛЕДНИЙ МОМЕНТ
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
            target.playSound(target.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.6f, 0.4f);

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
