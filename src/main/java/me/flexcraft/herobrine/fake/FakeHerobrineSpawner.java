package me.flexcraft.herobrine.fake;

import me.flexcraft.herobrine.HerobrinePlugin;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FakeHerobrineSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        World world = target.getWorld();

        // 👉 СПАВН ПРЯМО ПЕРЕД ЛИЦОМ
        Location spawnLoc = target.getEyeLocation()
                .add(target.getLocation().getDirection().normalize().multiply(1.2));
        spawnLoc.setPitch(0);
        spawnLoc.setYaw(target.getLocation().getYaw() + 180);

        Villager herobrine = world.spawn(spawnLoc, Villager.class);
        herobrine.setCustomName("§fHerobrine");
        herobrine.setCustomNameVisible(true);
        herobrine.setAI(false);
        herobrine.setSilent(true);
        herobrine.setInvulnerable(true);

        // 👁️ БЕЛЫЕ ГЛАЗА (кастомная голова)
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwner("MHF_Herobrine"); // классический Herobrine-скин
            head.setItemMeta(meta);
        }

        EntityEquipment eq = herobrine.getEquipment();
        if (eq != null) {
            eq.setHelmet(head);
        }

        // 😱 ЭФФЕКТЫ СТРАХА
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 1));

        // 🔊 ЗВУКИ (шёпот + пещера)
        world.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1.5f, 0.4f);
        world.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.8f, 0.5f);

        // ⚡ МИКРО-ВСПЫШКА (имитация белых глаз)
        target.spawnParticle(Particle.FLASH, target.getEyeLocation(), 1);

        // 🧟‍♂️ МЕДЛЕННО ИДЁТ К ИГРОКУ
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks > 60 || herobrine.isDead()) {
                    disappear();
                    return;
                }

                // если игрок отвернулся — ИСЧЕЗАЕТ
                Vector look = target.getLocation().getDirection();
                Vector toHerobrine = herobrine.getLocation()
                        .toVector()
                        .subtract(target.getLocation().toVector())
                        .normalize();

                if (look.dot(toHerobrine) < 0.6) {
                    disappear();
                    return;
                }

                // движение к игроку
                Vector move = target.getLocation()
                        .toVector()
                        .subtract(herobrine.getLocation().toVector())
                        .normalize()
                        .multiply(0.08);

                herobrine.teleport(herobrine.getLocation().add(move));
                ticks++;
            }

            void disappear() {
                world.spawnParticle(
                        Particle.SMOKE_LARGE,
                        herobrine.getLocation().add(0, 1, 0),
                        25
                );
                world.playSound(
                        herobrine.getLocation(),
                        Sound.ENTITY_WITHER_SPAWN,
                        0.6f,
                        0.3f
                );
                herobrine.remove();
                cancel();
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
