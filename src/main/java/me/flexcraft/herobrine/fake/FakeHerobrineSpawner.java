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
                .add(target.getLocation().getDirection().normalize().multiply(1.4));
        spawnLoc.setYaw(target.getLocation().getYaw() + 180);
        spawnLoc.setPitch(0);

        Villager herobrine = world.spawn(spawnLoc, Villager.class);
        herobrine.setCustomName("§fHerobrine");
        herobrine.setCustomNameVisible(true);
        herobrine.setAI(false);
        herobrine.setSilent(true);
        herobrine.setInvulnerable(true);

        // 👁️ БЕЛЫЕ ГЛАЗА
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwner("MHF_Herobrine");
            head.setItemMeta(meta);
        }

        EntityEquipment eq = herobrine.getEquipment();
        if (eq != null) {
            eq.setHelmet(head);
        }

        // 😱 ЭФФЕКТЫ СТРАХА
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 1));

        // 🔊 ЗВУКИ
        world.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1.5f, 0.4f);
        world.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1.0f, 0.5f);

        // ⚡ ВСПЫШКА (глаза)
        target.spawnParticle(Particle.FLASH, target.getEyeLocation(), 1);

        new BukkitRunnable() {

            int ticks = 0;

            @Override
            public void run() {

                if (!herobrine.isValid()) {
                    cancel();
                    return;
                }

                // ⏱ ПЕРВЫЕ 30 ТИКОВ (1.5 сек) — НЕ ИСЧЕЗАЕТ НИ ПРИ КАКИХ УСЛОВИЯХ
                if (ticks < 30) {
                    ticks++;
                    return;
                }

                // 👁️ ПОСЛЕ — ПРОВЕРКА ВЗГЛЯДА
                Vector look = target.getLocation().getDirection().normalize();
                Vector toHerobrine = herobrine.getLocation()
                        .toVector()
                        .subtract(target.getLocation().toVector())
                        .normalize();

                if (look.dot(toHerobrine) < 0.5) {
                    disappear();
                    return;
                }

                // 🧟‍♂️ МЕДЛЕННО ПОДХОДИТ
                Vector move = target.getLocation()
                        .toVector()
                        .subtract(herobrine.getLocation().toVector())
                        .normalize()
                        .multiply(0.06);

                herobrine.teleport(herobrine.getLocation().add(move));

                ticks++;

                // ⏳ ЛИМИТ ЖИЗНИ
                if (ticks > 80) {
                    disappear();
                }
            }

            void disappear() {
                world.spawnParticle(
                        Particle.SMOKE_LARGE,
                        herobrine.getLocation().add(0, 1, 0),
                        30
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
