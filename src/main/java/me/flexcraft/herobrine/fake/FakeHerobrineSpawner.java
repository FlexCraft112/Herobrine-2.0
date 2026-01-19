package me.flexcraft.herobrine.fake;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FakeHerobrineSpawner {

    /**
     * Возвращает точку СПЕРЕД игроком (а не за спиной)
     */
    public static Location getLocationInFront(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize();

        return eye.add(direction.multiply(distance));
    }
}
