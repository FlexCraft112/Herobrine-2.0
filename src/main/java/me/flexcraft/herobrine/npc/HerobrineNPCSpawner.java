package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location spawnLoc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        spawnLoc.setY(target.getLocation().getY());

        // ВНУТРЕННЕЕ ИМЯ NPC
        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, "BalloonLion9289");

        npc.spawn(spawnLoc);
        npc.setProtected(true);

        // ❌ ПОЛНОСТЬЮ УБИРАЕМ НИК
        npc.setName("");
        npc.data().setPersistent("nameplate-visible", false);

        // СКИН = СТИВ (БЕЗ ГОЛОВЫ, БЕЗ КОСТЫЛЕЙ)
        npc.data().setPersistent("player-skin-name", "Steve");
        npc.data().setPersistent("player-skin-use-latest", true);

        // ===== ЭФФЕКТЫ УЖАСА =====
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // ===== СМОТРИТ ЧЁТКО В ГЛАЗА (БЕЗ ЗАДИРАНИЯ ВВЕРХ) =====
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned()) {
                    cancel();
                    return;
                }

                Location eye = target.getEyeLocation().clone();
                eye.setPitch(0); // 🔥 КЛЮЧ: убираем взгляд вверх

                npc.faceLocation(eye);
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // ===== ЭФФЕКТНОЕ ИСЧЕЗНОВЕНИЕ =====
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = npc.getEntity().getLocation();

                loc.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        loc.add(0, 1, 0),
                        60,
                        0.4, 0.8, 0.4,
                        0.02
                );

                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 0.3f);

                npc.despawn();
                npc.destroy();
            }
        }.runTaskLater(plugin, 80L); // 4 секунды
    }
}
