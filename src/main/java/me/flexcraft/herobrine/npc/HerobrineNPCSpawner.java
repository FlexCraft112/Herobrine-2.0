package me.flexcraft.herobrine.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HerobrineNPCSpawner {

    private static NPC activeNPC = null;

    public static boolean isActive() {
        return activeNPC != null && activeNPC.isSpawned();
    }

    public static void spawn(Plugin plugin, Player target) {

        if (isActive()) return;

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        NPC npc = registry.createNPC(EntityType.PLAYER, "BalloonLion9289");
        activeNPC = npc;

        // 📍 Точка спавна: 2.5 блока перед игроком + 1 блок вверх
        Location spawnLoc = target.getLocation().clone();
        Vector dir = spawnLoc.getDirection().normalize().multiply(2.5);
        spawnLoc.add(dir);
        spawnLoc.add(0, 1.0, 0);
        spawnLoc.setYaw(target.getLocation().getYaw() + 180);
        spawnLoc.setPitch(0);

        npc.spawn(spawnLoc);

        Entity entity = npc.getEntity();
        if (entity == null) return;

        // ❌ Убираем имя и HP
        entity.setCustomNameVisible(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);

        // 👁️ Эффекты ужаса
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // 💀 Голова Herobrine (MHF)
        if (entity instanceof org.bukkit.entity.LivingEntity living) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwner("MHF_Herobrine");
                head.setItemMeta(meta);
            }
            living.getEquipment().setHelmet(head);
        }

        // 🌫️ Дым при появлении
        entity.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                spawnLoc.clone().add(0, 1.2, 0),
                40,
                0.4, 0.6, 0.4,
                0.02
        );

        // ⏳ Исчезновение через 3 секунды
        new BukkitRunnable() {
            @Override
            public void run() {

                if (!npc.isSpawned()) {
                    activeNPC = null;
                    return;
                }

                Location loc = npc.getEntity().getLocation();

                // 🌑 Адский дым
                loc.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        loc.clone().add(0, 1.2, 0),
                        80,
                        0.6, 0.8, 0.6,
                        0.03
                );

                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1f, 0.3f);

                npc.despawn();
                npc.destroy();
                activeNPC = null;
            }
        }.runTaskLater(plugin, 60L); // 3 секунды
    }
}
