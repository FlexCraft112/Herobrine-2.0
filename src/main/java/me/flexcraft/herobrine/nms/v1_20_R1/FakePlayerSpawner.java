package me.flexcraft.herobrine.nms.v1_20_R1;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;

import java.util.UUID;

public class FakePlayerSpawner {

    public static void spawn(org.bukkit.entity.Player target) {

        CraftPlayer cp = (CraftPlayer) target;
        MinecraftServer server = ((CraftServer) cp.getServer()).getServer();
        ServerLevel level = ((CraftWorld) target.getWorld()).getHandle();

        Location l = target.getLocation();
        Location spawn = l.clone().add(l.getDirection().normalize().multiply(3));

        GameProfile profile = new GameProfile(UUID.randomUUID(), "Herobrine");

        ServerPlayer fake = new ServerPlayer(server, level, profile);

        fake.setPos(spawn.getX(), spawn.getY(), spawn.getZ());
        fake.setYRot(l.getYaw() + 180);
        fake.setXRot(0);

        // TAB-лист
        cp.getHandle().connection.send(
                new ClientboundPlayerInfoUpdatePacket(
                        ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        fake
                )
        );

        // СПАВН
        cp.getHandle().connection.send(new ClientboundAddPlayerPacket(fake));

        // ВЗГЛЯД В ЛИЦО
        cp.getHandle().connection.send(
                new ClientboundRotateHeadPacket(
                        fake,
                        (byte) (fake.getYRot() * 256 / 360)
                )
        );
    }
}
