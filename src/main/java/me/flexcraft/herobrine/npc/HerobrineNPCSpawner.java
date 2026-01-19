package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        // На всякий случай — удаляем старого Херобрина
        for (NPC npc : registry) {
            if (npc.getName().equalsIgnoreCase("Herobrine")) {
                npc.destroy();
            }
        }

        // Создаём NPC
        NPC npc = registry.createNPC(EntityType.PLAYER, "Herobrine");

        // Бессмертный
        npc.setProtected(true);

        // Всегда смотрит в глаза
        LookClose lookClose = new LookClose();
        lookClose.lookClose(true);
        lookClose.setRange(50);
        npc.addTrait(lookClose);

        // Скин Herobrine (белые глаза)
        npc.data().setPersistent("player-skin-name", "Herobrine");
        npc.data().setPersistent("player-skin-use-latest", true);

        // Спавн ПЕРЕД игроком
        Location spawnLoc = target.getLocation()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        spawnLoc.setYaw(target.getLocation().getYaw() + 180);
        spawnLoc.setPitch(0);

        npc.spawn(spawnLoc);

        // Хоррор-звуки
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // Эффекты страха
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        // Дым
        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                spawnLoc.clone().add(0, 1.8, 0),
                30,
                0.2, 0.3, 0.2,
                0.01
        );

        // Исчезает как призрак
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    npc.despawn();
                    npc.destroy();
                }
            }
        }.runTaskLater(plugin, 60L); // 3 секунды
    }
}
