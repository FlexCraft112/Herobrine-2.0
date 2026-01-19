package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.LookClose;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        // 📍 Появляется ПРЯМО ПЕРЕД ЛИЦОМ
        Location spawnLoc = target.getLocation()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        spawnLoc.add(0, 1, 0);

        // 🧍 NPC
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(
                EntityType.PLAYER,
                ChatColor.DARK_GRAY + "Herobrine"
        );

        // 👁️ Белые глаза (рабочий UUID)
        npc.data().setPersistent("player-skin-uuid",
                "069a79f4-44e9-4726-a5be-fca90e38aaf5");
        npc.data().setPersistent("player-skin-use-latest", true);

        // 🧠 СМОТРИТ В ГЛАЗА
        LookClose look = npc.getOrAddTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(64);

        // ☠️ Бессмертен
        npc.setProtected(true);
        npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, true);

        npc.spawn(spawnLoc);

        // 😈 ХОРРОР ПРИ ПОЯВЛЕНИИ
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.3f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.4f);

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                spawnLoc.clone().add(0, 1.8, 0),
                40,
                0.3, 0.4, 0.3,
                0.01
        );

        // ⏳ ИСЧЕЗНОВЕНИЕ ЧЕРЕЗ 3 СЕКУНДЫ
        new BukkitRunnable() {
            @Override
            public void run() {

                if (!npc.isSpawned()) return;

                Location loc = npc.getEntity().getLocation();

                // 🌑 АДСКИЙ ДЫМ
                loc.getWorld().spawnParticle(
                        Particle.CAMPFIRE_COSY_SMOKE,
                        loc.clone().add(0, 1, 0),
                        120,
                        0.6, 1.0, 0.6,
                        0.01
                );

                loc.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        loc.clone().add(0, 1.5, 0),
                        80,
                        0.4, 0.6, 0.4,
                        0.01
                );

                // ⚡ ТРЕСК И ПУСТОТА
                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.3f);
                loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.2f);

                // 🕳️ ДОП. СЛЕПОТА ПОСЛЕ ИСЧЕЗНОВЕНИЯ
                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 0));

                npc.despawn();
                npc.destroy();
            }
        }.runTaskLater(plugin, 60L); // 3 секунды
    }
}
