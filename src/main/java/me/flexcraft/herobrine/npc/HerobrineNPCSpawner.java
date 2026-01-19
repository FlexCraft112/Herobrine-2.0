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

        // ➡️ позиция: 2.5 блока впереди + 1 вверх
        Location loc = target.getLocation().clone();
        Vector dir = loc.getDirection().normalize().multiply(2.5);
        loc.add(dir).add(0, 1, 0);
        loc.setYaw(target.getLocation().getYaw());
        loc.setPitch(0);

        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        npc.spawn(loc);

        // ❌ скрываем всё визуальное
        npc.setName("");
        npc.data().setPersistent("nameplate-visible", false);
        npc.data().setPersistent("tablist", false);
        npc.data().setPersistent("show-health", false);

        // 👁️ LOOK
        npc.addTrait(LookClose.class);
        LookClose look = npc.getTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(6);
        look.setRandomLook(false);

        // 🎭 Голова Херобрина
        equipHerobrineHead();

        // 🌫️ появление
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 0.4, 0.6, 0.4, 0.01);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.5f);

        // 🕶️ ПОСТОЯННАЯ ТЬМА (ФОН, НЕ ПРОПАДАЕТ)
        applyBackgroundDarkness(target);

        // 😨 стартовая паника
        applyPanicEffects(target);

        // 💀 волны страха
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!active) {
                task.cancel();
                return;
            }

            applyPanicEffects(target);

            target.getWorld().spawnParticle(
                    Particle.SMOKE_NORMAL,
                    target.getLocation().add(0, 1.2, 0),
                    12, 0.3, 0.4, 0.3, 0.01
            );

            target.playSound(
                    target.getLocation(),
                    Sound.ENTITY_ENDERMAN_STARE,
                    0.5f,
                    0.4f
            );

        }, 60L, 80L); // каждые ~4 секунды

        sendScaryMessages(plugin, target);

        // ⏳ исчезновение
        Bukkit.getScheduler().runTaskLater(plugin, HerobrineNPCSpawner::despawn, 20 * 20L);
    }

    // 🌑 ФОН: тьма без "чистого обзора"
    private static void applyBackgroundDarkness(Player p) {
        p.addPotionEffect(new PotionEffect(
                PotionEffectType.DARKNESS,
                20 * 30, // 30 секунд
                0,
                false,
                false
        ));
    }

    // 😵 ПАНИКА
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

    private static void equipHerobrineHead() {
        if (!npc.isSpawned()) return;
        if (!(npc.getEntity() instanceof org.bukkit.entity.LivingEntity entity)) return;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Herobrine"));
        head.setItemMeta(meta);

        entity.getEquipment().setHelmet(head);
    }

    private static void sendScaryMessages(JavaPlugin plugin, Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§8§oТы не должен был его увидеть..."), 40L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§7§oОн всё ещё здесь."), 80L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§4§lНЕ СМОТРИ"), 120L);
    }

    public static void despawn() {
        if (npc != null && npc.isSpawned()) {
            Location loc = npc.getEntity().getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 50, 0.5, 0.7, 0.5, 0.01);
            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 0.6f, 0.6f);
            npc.despawn();
            npc.destroy();
        }
        npc = null;
        active = false;
    }
}
