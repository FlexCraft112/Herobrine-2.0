package me.flexcraft.herobrine.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class HerobrineNPCSpawner {

    private static NPC npc;
    private static boolean active = false;
    private static int effectTaskId = -1;

    public static boolean isActive() {
        return active;
    }

    public static void spawn(JavaPlugin plugin, Player target) {
        if (active) return;
        active = true;

        // =========================
        // 1️⃣ СПЕРЕДИ — БЕЗ МЕЧА
        // =========================
        Location front = getInFront(target, 2.5, 1);
        spawnNPC(front, false);

        front.getWorld().spawnParticle(Particle.SMOKE_LARGE, front, 40, 0.3, 0.5, 0.3, 0.01);
        front.getWorld().playSound(front, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.5f);

        startScaryEffects(plugin, target);
        sendScaryMessages(plugin, target);

        // =========================
        // 2️⃣ ЧЕРЕЗ 4 СЕК — СЗАДИ С МЕЧОМ
        // =========================
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            despawnInternal();

            Location back = getBehind(target, 1.5, 1);
            spawnNPC(back, true); // ← МЕЧ ТОЛЬКО ЗДЕСЬ

            back.getWorld().spawnParticle(Particle.SMOKE_LARGE, back, 50, 0.3, 0.6, 0.3, 0.01);
            back.getWorld().playSound(back, Sound.ENTITY_ENDERMAN_SCREAM, 0.8f, 0.4f);

            // =========================
            // 3️⃣ УДАР НА 5-Й СЕКУНДЕ
            // =========================
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> hitPlayer(target),
                    100L // 5 секунд после появления сзади
            );

            // =========================
            // 4️⃣ ИСЧЕЗНОВЕНИЕ НА 7-Й СЕКУНДЕ
            // =========================
            Bukkit.getScheduler().runTaskLater(plugin,
                    HerobrineNPCSpawner::despawn,
                    140L
            );

        }, 80L); // 4 секунды после появления спереди
    }

    // =========================
    // SPAWN NPC
    // =========================
    private static void spawnNPC(Location loc, boolean withSword) {
        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        npc.spawn(loc);

        npc.setName("");
        npc.data().setPersistent("nameplate-visible", false);
        npc.data().setPersistent("tablist", false);
        npc.data().setPersistent("show-health", false);

        npc.addTrait(LookClose.class);
        LookClose look = npc.getTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(6);
        look.setRandomLook(false);

        if (npc.getEntity() instanceof LivingEntity entity) {
            equipHerobrine(entity, withSword);
        }
    }

    // =========================
    // СНАРЯЖЕНИЕ
    // =========================
    private static void equipHerobrine(LivingEntity entity, boolean withSword) {
        // Голова
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Herobrine"));
        head.setItemMeta(meta);

        entity.getEquipment().setHelmet(head);

        // Меч — ТОЛЬКО если нужно
        if (withSword) {
            ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
            entity.getEquipment().setItemInMainHand(sword);
        } else {
            entity.getEquipment().setItemInMainHand(null);
        }
    }

    // =========================
    // УДАР
    // =========================
    private static void hitPlayer(Player p) {
        if (!p.isOnline() || !active) return;

        p.damage(3.0); // 1.5 сердца
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 0.6f);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);

        p.sendMessage("§4§oОн был прямо за тобой...");
    }

    // =========================
    // ЭФФЕКТЫ (ПОСТОЯННО)
    // =========================
    private static void startScaryEffects(JavaPlugin plugin, Player p) {
        effectTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!active || !p.isOnline()) return;

            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.BLINDNESS, 40, 1, false, false));

            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOW, 40, 2, false, false));

        }, 0L, 20L);
    }

    // =========================
    // СООБЩЕНИЯ
    // =========================
    private static void sendScaryMessages(JavaPlugin plugin, Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§8§oТы слышишь дыхание..."), 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§7§oОн ближе, чем кажется."), 60L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§4§lПОЗДНО"), 100L);
    }

    // =========================
    // DESPAWN
    // =========================
    public static void despawn() {
        if (effectTaskId != -1) {
            Bukkit.getScheduler().cancelTask(effectTaskId);
            effectTaskId = -1;
        }

        if (npc != null && npc.isSpawned()) {
            Location loc = npc.getEntity().getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 60, 0.4, 0.6, 0.4, 0.01);
            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 0.7f, 0.6f);
            npc.despawn();
            npc.destroy();
        }
        npc = null;
        active = false;
    }

    private static void despawnInternal() {
        if (npc != null && npc.isSpawned()) {
            npc.despawn();
            npc.destroy();
        }
        npc = null;
    }

    // =========================
    // ПОЗИЦИИ
    // =========================
    private static Location getInFront(Player p, double distance, double y) {
        Location l = p.getLocation().clone();
        Vector dir = l.getDirection().normalize().multiply(distance);
        l.add(dir);
        l.add(0, y, 0);
        l.setPitch(0);
        return l;
    }

    private static Location getBehind(Player p, double distance, double y) {
        Location l = p.getLocation().clone();
        Vector dir = l.getDirection().normalize().multiply(-distance);
        l.add(dir);
        l.add(0, y, 0);
        l.setPitch(0);
        return l;
    }
}
