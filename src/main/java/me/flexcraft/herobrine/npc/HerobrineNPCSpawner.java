package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location spawnLoc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        spawnLoc.setY(target.getLocation().getY());

        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, "Herobrine");

        npc.spawn(spawnLoc);
        npc.setProtected(true);

        // ТЕЛО = СТИВ
        npc.data().setPersistent("player-skin-name", "Steve");
        npc.data().setPersistent("player-skin-use-latest", true);

        // ===== ГОЛОВА ХЕРОБРИНА =====
        if (npc.getEntity() instanceof LivingEntity le) {

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwner("Herobrine");
            head.setItemMeta(meta);

            le.getEquipment().setHelmet(head);
        }

        // ===== ЭФФЕКТЫ УЖАСА =====
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // ===== СМОТРИТ ПРЯМО В ГЛАЗА =====
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned()) {
                    cancel();
                    return;
                }
                npc.faceLocation(target.getEyeLocation());
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // ===== ИСЧЕЗНОВЕНИЕ С ЧЁРНЫМ ДЫМОМ =====
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = npc.getEntity().getLocation();

                loc.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        loc.add(0, 1, 0),
                        50,
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
