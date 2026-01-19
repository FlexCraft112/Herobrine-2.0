package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location loc = target.getLocation().add(target.getLocation().getDirection().normalize().multiply(2));
        loc.setY(target.getLocation().getY());

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Herobrine");
        npc.spawn(loc);

        // Делаем бессмертным
        npc.setProtected(true);

        // Одеваем голову Херобрина
        Equipment eq = npc.getOrAddTrait(Equipment.class);
        eq.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.PLAYER_HEAD));

        // Звуки + эффекты
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        target.spawnParticle(Particle.SMOKE_LARGE, loc.clone().add(0, 1.8, 0), 40, 0.3, 0.5, 0.3, 0.01);

        // Постоянный взгляд в глаза (БЕЗ LookClose)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned() || !target.isOnline()) {
                    cancel();
                    return;
                }
                npc.faceLocation(target.getEyeLocation());
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Исчезновение с ЧЁРНЫМ ДЫМОМ
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    Location l = npc.getEntity().getLocation();
                    l.getWorld().spawnParticle(Particle.SMOKE_LARGE, l.add(0, 1, 0), 80, 0.4, 0.8, 0.4, 0.02);
                    l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.3f);
                    npc.despawn();
                    npc.destroy();
                }
            }
        }.runTaskLater(plugin, 60L); // 3 секунды
    }
}
