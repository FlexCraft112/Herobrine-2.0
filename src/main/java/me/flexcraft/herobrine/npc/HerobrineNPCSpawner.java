package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.LookClose;
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

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        Location spawnLoc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(2));

        spawnLoc.setY(
                spawnLoc.getWorld().getHighestBlockAt(spawnLoc).getY() + 1
        );

        NPC npc = registry.createNPC(EntityType.PLAYER, "§7Herobrine");

        npc.data().setPersistent("protected", true);
        npc.data().setPersistent("collidable", false);

        npc.spawn(spawnLoc);

        // 👁 СМОТРИТ В ГЛАЗА
        LookClose look = npc.getOrAddTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(10);

        // 🧠 ГОЛОВА HEROBRINE
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // ⚠️ UUID любого фейкового профиля
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
        ));

        // 👉 ИМЯ СКИНА С БЕЛЫМИ ГЛАЗАМИ
        meta.setOwner("Herobrine");

        head.setItemMeta(meta);

        Equipment eq = npc.getOrAddTrait(Equipment.class);
        eq.set(Equipment.EquipmentSlot.HELMET, head);

        // ☠ ХОРРОР ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.3f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                spawnLoc.clone().add(0, 1.6, 0),
                40,
                0.4, 0.6, 0.4,
                0.02
        );

        // 💨 ИСЧЕЗНОВЕНИЕ
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!npc.isSpawned()) return;

                Location loc = npc.getEntity().getLocation();

                loc.getWorld().spawnParticle(
                        Particle.CAMPFIRE_SIGNAL_SMOKE,
                        loc.clone().add(0, 1.2, 0),
                        120,
                        0.6, 0.8, 0.6,
                        0.01
                );

                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.2f);

                npc.despawn();
                npc.destroy();
            }
        }.runTaskLater(plugin, 60L);
    }
}
