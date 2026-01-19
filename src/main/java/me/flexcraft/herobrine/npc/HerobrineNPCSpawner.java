package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    private static boolean active = false;
    private static NPC currentNPC = null;

    public static boolean isActive() {
        return active;
    }

    public static void spawn(HerobrinePlugin plugin, Player target) {

        if (active) return;

        active = true;

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(EntityType.PLAYER, "");
        currentNPC = npc;

        Location spawnLoc = target.getLocation().clone();
        spawnLoc.setY(spawnLoc.getBlockY() + 1.2);

        npc.spawn(spawnLoc);

        // --- СКРЫВАЕМ ИМЯ / TAB / HP ---
        npc.setName("");
        npc.data().setPersistent("nameplate-visible", false);
        npc.data().setPersistent("tablist", false);

        // --- LOOK TRAIT (/npc look) ---
        LookClose look = npc.getOrAddTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(12);
        look.setRealisticLooking(true);

        // --- ENTITY ---
        LivingEntity le = (LivingEntity) npc.getEntity();
        le.setAI(false);
        le.setSilent(true);
        le.setInvulnerable(true);
        le.setCollidable(false);

        // --- ГОЛОВА HEROBRINE ---
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwner("MHF_Herobrine");
            head.setItemMeta(meta);
        }
        le.getEquipment().setHelmet(head);

        // --- ЭФФЕКТЫ ---
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        target.sendMessage("§8Вы чувствуете §fчужой взгляд...");

        // --- ИСЧЕЗНОВЕНИЕ ---
        new BukkitRunnable() {
            @Override
            public void run() {

                Location loc = le.getLocation();

                loc.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        loc.clone().add(0, 1, 0),
                        60,
                        0.4, 0.6, 0.4,
                        0.03
                );

                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1f, 0.2f);

                npc.despawn();
                npc.destroy();

                // 🔓 РАЗРЕШАЕМ СНОВА
                active = false;
                currentNPC = null;
            }
        }.runTaskLater(plugin, 60L);
    }
}
