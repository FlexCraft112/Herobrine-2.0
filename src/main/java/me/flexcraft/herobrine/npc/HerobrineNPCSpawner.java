package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        Location spawnLoc = target.getLocation()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        spawnLoc.setY(target.getLocation().getY());

        NPC npc = registry.createNPC(EntityType.PLAYER, "§7Herobrine");

        npc.data().setPersistent("player-skin-name", "Herobrine");
        npc.data().setPersistent("player-skin-use-latest", true);
        npc.data().setPersistent("protected", true);

        npc.spawn(spawnLoc);

        // 🔊 ХОРРОР ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                spawnLoc.clone().add(0, 1.8, 0),
                30,
                0.3, 0.4, 0.3,
                0.01
        );

        // 👁️ ВСЕГДА СМОТРИТ В ГЛАЗА
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!npc.isSpawned() || !target.isOnline()) {
                    cancel();
                    return;
                }

                Location npcLoc = npc.getEntity().getLocation();
                Location eye = target.getEyeLocation();

                npcLoc.setDirection(eye.toVector().subtract(npcLoc.toVector()));
                npc.getEntity().teleport(npcLoc);

                ticks++;

                // 💨 ИСЧЕЗНОВЕНИЕ С ДЫМОМ
                if (ticks >= 60) {
                    Location loc = npc.getEntity().getLocation();

                    loc.getWorld().spawnParticle(
                            Particle.CAMPFIRE_COSY_SMOKE,
                            loc.add(0, 1.2, 0),
                            80,
                            0.4, 0.6, 0.4,
                            0.02
                    );

                    loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.3f);

                    npc.despawn();
                    npc.destroy();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
