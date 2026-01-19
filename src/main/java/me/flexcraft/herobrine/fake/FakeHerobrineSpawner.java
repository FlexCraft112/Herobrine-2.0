package me.flexcraft.herobrine.fake;

import me.flexcraft.herobrine.HerobrinePlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class FakeHerobrineSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location base = target.getLocation();
        Location spawnLoc = base.clone().add(
                base.getDirection().normalize().multiply(1.5)
        );

        spawnLoc.setY(base.getY());

        // 🔥 СПАВН ХЕРОБРИНА
        Villager herobrine = target.getWorld().spawn(spawnLoc, Villager.class);

        herobrine.setCustomName("§fHerobrine");
        herobrine.setCustomNameVisible(true);
        herobrine.setAI(false);
        herobrine.setSilent(true);
        herobrine.setInvulnerable(true); // 💀 БЕССМЕРТНЫЙ
        herobrine.setGravity(false);
        herobrine.setCollidable(false);

        // ❌ УБИРАЕМ ПРОФЕССИЮ (чтобы не выглядел как житель)
        herobrine.setProfession(Villager.Profession.NONE);

        // 👁️ БЕЛЫЕ ГЛАЗА (через эффект)
        herobrine.addPotionEffect(new PotionEffect(
                PotionEffectType.GLOWING,
                40,
                1,
                false,
                false
        ));

        // 😱 ЭФФЕКТЫ ХОРРОРА ИГРОКУ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 10));

        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.6f);

        // 👁️ ПОВОРАЧИВАЕМ ЛИЦОМ К ИГРОКУ
        new BukkitRunnable() {
            @Override
            public void run() {
                Location look = target.getLocation().clone();
                look.setDirection(
                        target.getLocation().toVector()
                                .subtract(herobrine.getLocation().toVector())
                );
                herobrine.teleport(look);
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // 💨 ИСЧЕЗНОВЕНИЕ
        new BukkitRunnable() {
            @Override
            public void run() {
                herobrine.remove();
                target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.4f);
            }
        }.runTaskLater(plugin, 40L);
    }
}
