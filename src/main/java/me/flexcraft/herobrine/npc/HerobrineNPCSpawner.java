package me.flexcraft.herobrine.npc;

import me.flexcraft.herobrine.HerobrinePlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HerobrineNPCSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        Location spawnLoc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(2));
        spawnLoc.setPitch(0);
        spawnLoc.setYaw(target.getLocation().getYaw() + 180);
        spawnLoc.setY(spawnLoc.getBlockY());

        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, "BalloonLion9289");

        npc.spawn(spawnLoc);

        // 🔒 ФИКС ПРОВАЛИВАНИЯ
        npc.data().setPersistent("gravity", false);
        npc.data().setPersistent("collidable", false);
        npc.teleport(spawnLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);

        // ❌ НИК И HP
        npc.setName("");
        npc.data().setPersistent("nameplate-visible", false);

        npc.setProtected(true);

        Player npcPlayer = (Player) npc.getEntity();

        // 💀 УБИРАЕМ СЕРДЦА НА 100%
        npcPlayer.setHealth(0.0001);
        npcPlayer.setInvulnerable(true);

        // 🧍 СКИН ТЕЛА — STEVE
        npc.data().setPersistent("player-skin-name", "Steve");
        npc.data().setPersistent("player-skin-use-latest", true);

        // 👁️ ГОЛОВА MHF_HEROBRINE
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner("MHF_Herobrine");
        head.setItemMeta(meta);
        npcPlayer.getEquipment().setHelmet(head);

        // 🚫 НЕ СМОТРИТ НИ НА КОГО
        npcPlayer.setRotation(spawnLoc.getYaw(), 0);

        // 😱 ЭФФЕКТЫ
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 3));

        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1f, 0.4f);
        target.playSound(target.getLocation(), Sound.AMBIENT_CAVE, 1f, 0.5f);

        // 💨 ЭПИЧЕСКОЕ ИСЧЕЗНОВЕНИЕ
        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = npcPlayer.getLocation();

                loc.getWorld().spawnParticle(
                        Particle.SMOKE_LARGE,
                        loc.add(0, 1, 0),
                        120,
                        0.6, 1.3, 0.6,
                        0.02
                );

                loc.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 0.3f);

                npc.despawn();
                npc.destroy();
            }
        }.runTaskLater(plugin, 80L);
    }
}
