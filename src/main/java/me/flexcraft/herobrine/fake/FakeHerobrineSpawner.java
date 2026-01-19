package me.flexcraft.herobrine.fake;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FakeHerobrineSpawner {

    /**
     * Возвращает точку строго перед игроком
     * @param player игрок
     * @param distance дистанция в блоках
     */
    public static Location getLocationInFront(Player player, double distance) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection().normalize();

        Location spawn = eye.clone().add(direction.multiply(distance));
        spawn.setPitch(0);

        return spawn;
    }
}
