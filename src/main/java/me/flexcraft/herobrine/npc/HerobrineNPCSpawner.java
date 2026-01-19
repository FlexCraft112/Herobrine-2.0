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
import org.bukkit.util.Vector;

public class HerobrineNPCSpawner {

    // UUID реального скина Herobrine (белые глаза)
    private static final String HEROBRINE_SKIN_UUID = "069a79f4-44e9-4726-a5be-fca90e38aaf5";

    public static void spawn(HerobrinePlugin plugin, Player target) {

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        Location spawnLoc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(2));

        spawnLoc.setY(
                spawnLoc.getWorld()
                        .getHighestBlockAt(spawnLoc)
                        .getLocation()
                        .getY() + 1
        );

        NPC npc = registry.createNPC(EntityType.PLAYER, "§7Herobrine");

        // 🔥 СКИН HEROBRINE
        npc.data().setPersistent("player-skin-uuid", HEROBRINE_SKIN_UUID);
        npc.data().setPersistent("player-skin-use-latest", true);
        npc.data().setPersistent("protected", true);
        npc.data().setPersistent("collidable", false);

        npc.spawn(spawnLoc);

        // ☠ ХОРРОР ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                spawnLoc.clone().add(0, 1.6, 0),
                40,
                0.3, 0.4, 0.3,
                0.01
        );

        // 👁 СМОТРИТ ПРЯМО В ЛИЦО (НЕ ВВЕРХ)
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!npc.isSpawned() || !target.isOnline()) {
                    cancel();
                    return;
                }

                Location npcLoc = npc.getEntity().getLocation();

                Location lookTarget = target.getLocation().clone().add(0, 1.2, 0);

                Vector dir = lookTarget.toVector().subtract(npcLoc.toVector());
                npcLoc.setDirection(dir);
                npc.getEntity().teleport(npcLoc);

                ticks++;

                // 💨 ИСЧЕЗНОВЕНИЕ В ТЁМНОМ ДЫМУ
                if (ticks >= 60) {
                    Location loc = npc.getEntity().getLocation();

                    loc.getWorld().spawnParticle(
                            Particle.CAMPFIRE_COSY_SMOKE,
                            loc.clone().add(0, 1.3, 0),
                            100,
                            0.5, 0.7, 0.5,
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
