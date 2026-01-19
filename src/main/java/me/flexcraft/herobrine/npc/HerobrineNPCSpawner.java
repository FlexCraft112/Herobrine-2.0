package me.flexcraft.herobrine.npc;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
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

    public static boolean isActive() {
        return active;
    }

    public static void spawn(JavaPlugin plugin, Player target) {
        if (active) return;
        active = true;

        // ===== СПАВН СПЕРЕДИ =====
        Location front = getInFront(target, 2.5, 1);
        spawnNPC(front, false);

        // ЭФФЕКТЫ
        applyBackgroundDarkness(target);
        applyPanicEffects(target);

        front.getWorld().spawnParticle(Particle.SMOKE_LARGE, front, 30, 0.4, 0.6, 0.4, 0.01);
        front.getWorld().playSound(front, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.5f);

        sendScaryMessages(plugin, target);

        // ===== ИСЧЕЗНУТЬ + ПОЯВИТЬСЯ СЗАДИ =====
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            despawnInternal();

            Location back = getBehind(target, 2.5, 1);
            spawnNPC(back, true);

            back.getWorld().spawnParticle(Particle.SMOKE_LARGE, back, 40, 0.4, 0.6, 0.4, 0.01);
            back.getWorld().playSound(back, Sound.ENTITY_ENDERMAN_SCREAM, 0.7f, 0.4f);

        }, 80L); // ~4 секунды

        // ===== ФИНАЛЬНОЕ ИСЧЕЗНОВЕНИЕ =====
        Bukkit.getScheduler().runTaskLater(plugin, HerobrineNPCSpawner::despawn, 20 * 20L);
    }

    // ===================== NPC =====================

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

        equipHerobrineHead();
        if (withSword) equipSword();
    }

    private static void equipHerobrineHead() {
        if (!(npc.getEntity() instanceof org.bukkit.entity.LivingEntity entity)) return;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Herobrine"));
        head.setItemMeta(meta);

        entity.getEquipment().setHelmet(head);
    }

    private static void equipSword() {
        if (!(npc.getEntity() instanceof org.bukkit.entity.LivingEntity entity)) return;

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        entity.getEquipment().setItemInMainHand(sword);
    }

    // ===================== ЭФФЕКТЫ =====================

    private static void applyBackgroundDarkness(Player p) {
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.DARKNESS,
                20 * 30,
                0,
                false,
                false
        ));
    }

    private static void applyPanicEffects(Player p) {
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.BLINDNESS,
                40,
                1,
                false,
                false
        ));

        p.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW,
                60,
                1,
                false,
                false
        ));
    }

    // ===================== СООБЩЕНИЯ =====================

    private static void sendScaryMessages(JavaPlugin plugin, Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§8§oТы слышишь дыхание..."), 40L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§7§oОн ближе, чем кажется."), 80L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§4§lПОЗДНО"), 120L);
    }

    // ===================== ПОЗИЦИИ =====================

    private static Location getInFront(Player p, double dist, double y) {
        Location loc = p.getLocation().clone();
        Vector dir = loc.getDirection().normalize().multiply(dist);
        loc.add(dir).add(0, y, 0);
        loc.setYaw(p.getLocation().getYaw());
        loc.setPitch(0);
        return loc;
    }

    private static Location getBehind(Player p, double dist, double y) {
        Location loc = p.getLocation().clone();
        Vector dir = loc.getDirection().normalize().multiply(-dist);
        loc.add(dir).add(0, y, 0);
        loc.setYaw(p.getLocation().getYaw() + 180);
        loc.setPitch(0);
        return loc;
    }

    // ===================== УДАЛЕНИЕ =====================

    private static void despawnInternal() {
        if (npc != null && npc.isSpawned()) {
            npc.despawn();
            npc.destroy();
        }
        npc = null;
    }

    public static void despawn() {
        despawnInternal();
        active = false;
    }
}
