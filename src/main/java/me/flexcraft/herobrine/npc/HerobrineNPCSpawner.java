package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location loc = target.getLocation()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        loc.setY(target.getLocation().getY());

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Herobrine");
        npc.spawn(loc);
        npc.setProtected(true);

        // Steve body
        SkinTrait skin = npc.getOrAddTrait(SkinTrait.class);
        skin.setSkinName("Steve");

        // Herobrine head (white eyes)
        Equipment eq = npc.getOrAddTrait(Equipment.class);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(
                UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7")
        ));

        head.setItemMeta(meta);
        eq.set(Equipment.EquipmentSlot.HELMET, head);

        // Horror effects
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // Look into eyes
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

        // Smoke disappear
        new BukkitRunnable() {
            @Override
            public void run() {
                if (npc.isSpawned()) {
                    Location l = npc.getEntity().getLocation();
                    l.getWorld().spawnParticle(
                            Particle.SMOKE_LARGE,
                            l.add(0, 1, 0),
                            200,
                            0.6, 1.2, 0.6,
                            0.02
                    );
                    l.getWorld().playSound(l, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.3f);
                    npc.despawn();
                    npc.destroy();
                }
            }
        }.runTaskLater(plugin, 60L);
    }
}
