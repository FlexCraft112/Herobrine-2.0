package me.flexcraft.herobrine.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(JavaPlugin plugin, Player target) {

        Location loc = target.getLocation()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        loc.setY(target.getLocation().getY());

        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, "Herobrine");

        // 🔒 Бессмертие
        npc.setProtected(true);

        // 👁️ Всегда смотрит в глаза
        npc.addTrait(LookClose.class);
        npc.getTrait(LookClose.class).lookClose(true);
        npc.getTrait(LookClose.class).setRange(10);

        // 👻 Скин Херобрина (белые глаза)
        SkinTrait skin = npc.getTrait(SkinTrait.class);
        skin.setSkinName("Herobrine");

        npc.spawn(loc);

        // 🔊 Звуки ужаса
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.6f);

        // 😵 Эффекты страха
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        // 🌫️ Частицы
        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                loc.clone().add(0, 1.8, 0),
                30,
                0.3, 0.3, 0.3,
                0.01
        );

        // 👻 Исчезновение через 3 секунды
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    npc.despawn();
                    npc.destroy();
                }
            }
        }.runTaskLater(plugin, 60L);
    }
}
