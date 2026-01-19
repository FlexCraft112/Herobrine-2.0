package me.flexcraft.herobrine.fake;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.flexcraft.herobrine.HerobrinePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.util.Vector;

public class FakeHerobrineSpawner {

    public static void spawn(HerobrinePlugin plugin, Player target) {

        // 📍 СПАВН ПЕРЕД ИГРОКОМ
        Location spawnLoc = target.getLocation().clone();
        Vector dir = spawnLoc.getDirection().normalize();
        spawnLoc.add(dir.multiply(3));
        spawnLoc.setY(spawnLoc.getY());

        // 👤 ВРЕМЕННЫЙ "ХЕРОБРИН"
        Villager npc = target.getWorld().spawn(spawnLoc, Villager.class, v -> {
            v.setAI(false);
            v.setSilent(true);
            v.setInvulnerable(true);
            v.setCollidable(false);
            v.setCustomName("§5Herobrine");
            v.setCustomNameVisible(true);
        });

        // 👁️ ПОСТОЯННО СМОТРИТ НА ИГРОКА
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!npc.isValid() || !target.isOnline()) {
                task.cancel();
                return;
            }

            Location npcLoc = npc.getLocation();
            Location playerLoc = target.getLocation();

            Vector look = playerLoc.toVector().subtract(npcLoc.toVector());
            npcLoc.setDirection(look);
            npc.teleport(npcLoc);

        }, 0L, 2L);

        // 💀 ИСЧЕЗАЕТ ЧЕРЕЗ 3 СЕКУНДЫ
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (npc.isValid()) {
                npc.remove();
                target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.7f, 0.4f);
            }
        }, 60L);
    }
}
