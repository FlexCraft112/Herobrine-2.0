package me.flexcraft.herobrine.fake;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class FakeHerobrineSpawner {

    private static final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

    public static void spawn(Player target) {
        Location loc = target.getLocation().add(target.getLocation().getDirection().normalize().multiply(2));
        loc.setYaw(target.getLocation().getYaw() + 180);
        loc.setPitch(0);

        WrappedGameProfile profile = new WrappedGameProfile(
                UUID.randomUUID(),
                "Herobrine"
        );

        // ❗ Белые глаза (скин)
        profile.getProperties().put("textures",
                new WrappedSignedProperty(
                        "textures",
                        "ewogICJ0aW1lc3RhbXAiIDogMTYwMDAwMDAwMCwKICAicHJvZmlsZUlkIiA6ICIwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJIZXJvYnJpbmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiBmYWxzZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y2YzNjY2JkNDk4NzFlNTM1ZDY2OTJhZGU4ZWI3ODQxN2FhZTU4MTNhZmU4ZTk1ODZkNDY2MjQyYzI2OWYiCiAgICB9CiAgfQp9",
                        ""
                )
        );

        FakePlayerPackets.spawnFakePlayer(target, profile, loc);

        new BukkitRunnable() {
            @Override
            public void run() {
                FakePlayerPackets.destroyFakePlayer(target);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Herobrine-2.0"), 60L);
    }
}
