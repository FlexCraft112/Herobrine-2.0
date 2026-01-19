package me.flexcraft.herobrine.fake;

import me.flexcraft.herobrine.HerobrinePlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FakeHerobrineSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location spawnLoc = target.getLocation().add(
                target.getLocation().getDirection().normalize().multiply(1.5)
        );

        // ⛔ блокируем поворот — чтобы он был ЛИЦОМ
        spawnLoc.setYaw(target.getLocation().getYaw() + 180);
        spawnLoc.setPitch(0);

        World world = target.getWorld();

        Villager herobrine = world.spawn(spawnLoc, Villager.class);
        herobrine.setCustomName("§fHerobrine");
        herobrine.setCustomNameVisible(true);
        herobrine.setAI(false);
        herobrine.setSilent(true);
        herobrine.setInvulnerable(true);

        // 🧥 Внешний вид
        EntityEquipment eq = herobrine.getEquipment();
        if (eq != null) {
            eq.setHelmet(new ItemStack(Material.PLAYER_HEAD));
            eq.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
            eq.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
            eq.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        }

        // 😱 ХОРРОР ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 2));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 1));

        world.playSound(
                target.getLocation(),
                Sound.AMBIENT_CAVE,
                1.5f,
                0.5f
        );

        // 👁️ "БЕЛЫЕ ГЛАЗА" (имитация вспышкой)
        target.spawnParticle(
                Particle.FLASH,
                target.getEyeLocation(),
                1
        );

        // 💀 Исчезновение через 2 секунды
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!herobrine.isDead()) {
                    world.spawnParticle(
                            Particle.SMOKE_LARGE,
                            herobrine.getLocation().add(0, 1, 0),
                            20
                    );
                    herobrine.remove();
                }
            }
        }.runTaskLater(plugin, 40L);
    }
}
