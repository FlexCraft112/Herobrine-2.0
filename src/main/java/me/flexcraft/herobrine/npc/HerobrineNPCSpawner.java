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

        // ➡️ 2.5 блока ПЕРЕД игроком + 1 блок вверх
        Location loc = target.getLocation().clone();
        Vector dir = loc.getDirection().normalize().multiply(2.5);
        loc.add(dir);
        loc.add(0, 1, 0);
        loc.setYaw(target.getLocation().getYaw());
        loc.setPitch(0);

        npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
        npc.spawn(loc);

        // ❌ скрываем ник + HP + TAB
        npc.setName("");
        npc.data().setPersistent("nameplate-visible", false);
        npc.data().setPersistent("tablist", false);
        npc.data().setPersistent("show-health", false);

        // 👁️ LOOK (как /npc look)
        npc.addTrait(LookClose.class);
        LookClose look = npc.getTrait(LookClose.class);
        look.lookClose(true);
        look.setRange(6);
        look.setRandomLook(false);

        // 🎭 Голова MHF_Herobrine
        equipHerobrineHead();

        // 🌫️ эффект появления
        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 30, 0.3, 0.5, 0.3, 0.01);
        loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.6f, 0.5f);
        loc.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_STARE, 0.6f, 0.4f);

        // 😨 стартовые эффекты игроку
        applyEffects(target);

        // 😨 пугающие сообщения
        sendScaryMessages(plugin, target);

        // 🔁 ПОВТОР ЭФФЕКТОВ ПОКА ХЕРОБРИН СУЩЕСТВУЕТ
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!active) {
                task.cancel();
                return;
            }

            applyEffects(target);

            target.playSound(
                    target.getLocation(),
                    Sound.ENTITY_ENDERMAN_STARE,
                    0.5f,
                    0.5f
            );

        }, 80L, 80L); // каждые 4 секунды

        // ⏳ авто-исчезновение через 20 сек
        Bukkit.getScheduler().runTaskLater(plugin, HerobrineNPCSpawner::despawn, 20 * 20L);
    }

    // 🎭 Голова Херобрина
    private static void equipHerobrineHead() {
        if (!npc.isSpawned()) return;
        if (!(npc.getEntity() instanceof org.bukkit.entity.LivingEntity entity)) return;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Herobrine"));
        head.setItemMeta(meta);

        entity.getEquipment().setHelmet(head);
    }

    // 😵 ЭФФЕКТЫ ИГРОКУ (единый метод)
    private static void applyEffects(Player target) {
        target.addPotionEffect(new PotionEffect(
                PotionEffectType.BLINDNESS,
                40, // 2 сек
                1,
                false,
                false
        ));

        target.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOW,
                60, // 3 сек
                1,
                false,
                false
        ));
    }

    // 😨 СООБЩЕНИЯ
    private static void sendScaryMessages(JavaPlugin plugin, Player p) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§8§oТы чувствуешь чужое присутствие..."), 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§7§oКто-то стоит §fочень близко§7§o."), 60L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.sendMessage("§4§lНЕ ОБОРАЧИВАЙСЯ"), 100L);

        Bukkit.getScheduler().runTaskLater(plugin, () ->
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.7f, 0.4f), 100L);
    }

    // 💨 ИСЧЕЗНОВЕНИЕ
    public static void despawn() {
        if (npc != null && npc.isSpawned()) {
            Location loc = npc.getEntity().getLocation();
            loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 40, 0.4, 0.6, 0.4, 0.01);
            loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_DEATH, 0.6f, 0.6f);
            npc.despawn();
            npc.destroy();
        }
        npc = null;
        active = false;
    }
}
