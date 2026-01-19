package me.flexcraft.herobrine.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.LookClose;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(JavaPlugin plugin, Player target) {

        Location loc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        loc.setY(target.getLocation().getY());

        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, "§fHerobrine");

        npc.spawn(loc);

        // ===== ВСЕГДА СМОТРИТ В ГЛАЗА =====
        LookClose look = npc.getOrAddTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(100);

        // ===== БЕЛЫЕ ГЛАЗА (СКИН) =====
        npc.data().setPersistent("player-skin-name", "Herobrine");
        npc.data().setPersistent("player-skin-use-latest", true);

        // ===== БЕССМЕРТИЕ =====
        npc.data().setPersistent(NPC.Metadata.DAMAGE_OTHERS, false);
        npc.data().setPersistent(NPC.Metadata.COLLIDABLE, false);
        npc.data().setPersistent(NPC.Metadata.SILENT, true);

        // ===== ЗВУКИ ХОРРОРА =====
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // ===== ЭФФЕКТЫ =====
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        // ===== ДЫМ =====
        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                loc.clone().add(0, 1.8, 0),
                30,
                0.2, 0.3, 0.2,
                0.01
        );

        // ===== ИСЧЕЗНОВЕНИЕ =====
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
