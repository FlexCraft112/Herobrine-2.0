package me.flexcraft.herobrine.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HerobrineNPCSpawner {

    private static NPC npc;
    private static boolean active = false;

    public static boolean isActive() {
        return active;
    }

    public static void spawn(Plugin plugin, Player target) {

        active = true;

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        npc = registry.createNPC(EntityType.PLAYER, ""); // имя скрыто
        npc.setProtected(true);

        // ───────────── ЛОКАЦИЯ ─────────────
        Location loc = target.getLocation().clone();

        Vector dir = loc.getDirection().normalize();
        loc.add(dir.multiply(2.5)); // 2.5 блока перед игроком
        loc.add(0, 1, 0); // +1 вверх (чтобы не проваливался)

        npc.spawn(loc);

        // ───────────── ВНЕШНИЙ ВИД ─────────────
        npc.data().setPersistent("nameplate-visible", false); // скрыть ник
        npc.data().setPersistent("show-health", false);       // скрыть хп

        // надеть голову Herobrine
        if (npc.getEntity() instanceof org.bukkit.entity.Player npcPlayer) {

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwner("MHF_Herobrine");
                head.setItemMeta(meta);
            }

            npcPlayer.getEquipment().setHelmet(head);
        }

        // ───────────── ЭФФЕКТЫ И УЖАС ─────────────
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        target.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                loc.clone().add(0, 1.8, 0),
                30,
                0.4, 0.6, 0.4,
                0.01
        );

        // ───────────── ИСЧЕЗНОВЕНИЕ ─────────────
        new BukkitRunnable() {
            @Override
            public void run() {

                if (npc != null && npc.isSpawned()) {

                    Location l = npc.getEntity().getLocation();

                    l.getWorld().spawnParticle(
                            Particle.SMOKE_LARGE,
                            l.clone().add(0, 1, 0),
                            80,
                            0.6, 1, 0.6,
                            0.02
                    );

                    l.getWorld().playSound(l, Sound.ENTITY_WITHER_AMBIENT, 1f, 0.3f);

                    npc.despawn();
                    npc.destroy();
                }

                npc = null;
                active = false;
            }
        }.runTaskLater(plugin, 20L * 6); // 6 секунд
    }
}
