package me.flexcraft.herobrine.fake;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.PacketType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class FakeHerobrineSpawner {

    private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    public static void spawn(Player target) {
        try {
            Location loc = target.getLocation()
                    .add(target.getLocation().getDirection().normalize().multiply(3));

            int entityId = (int) (Math.random() * 100000);
            UUID uuid = UUID.randomUUID();

            WrappedGameProfile profile =
                    new WrappedGameProfile(uuid, "Herobrine");

            // PLAYER_INFO (ADD)
            PacketContainer info = manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            info.getPlayerInfoAction().write(0,
                    EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            info.getPlayerInfoDataLists().write(0,
                    Collections.singletonList(
                            new EnumWrappers.PlayerInfoData(
                                    profile, 0,
                                    EnumWrappers.NativeGameMode.SURVIVAL,
                                    null
                            )
                    )
            );
            manager.sendServerPacket(target, info);

            // SPAWN
            PacketContainer spawn = manager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawn.getIntegers().write(0, entityId);
            spawn.getUUIDs().write(0, uuid);
            spawn.getDoubles()
                    .write(0, loc.getX())
                    .write(1, loc.getY())
                    .write(2, loc.getZ());
            spawn.getBytes()
                    .write(0, (byte) (loc.getYaw() * 256 / 360))
                    .write(1, (byte) (loc.getPitch() * 256 / 360));
            manager.sendServerPacket(target, spawn);

            // METADATA — УБИРАЕТ КУБ
            PacketContainer meta = manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            meta.getIntegers().write(0, entityId);
            meta.getWatchableCollectionModifier().write(0, Collections.emptyList());
            manager.sendServerPacket(target, meta);

            // HEAD ROTATION
            PacketContainer head = manager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            head.getIntegers().write(0, entityId);
            head.getBytes().write(0, (byte) (loc.getYaw() * 256 / 360));
            manager.sendServerPacket(target, head);

            // AUTO DESPAWN через 4 секунды
            Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("Herobrine-2.0"),
                    () -> destroy(target, entityId, uuid),
                    80L
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void destroy(Player target, int entityId, UUID uuid) {
        try {
            PacketContainer destroy =
                    manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroy.getIntLists().write(0, Collections.singletonList(entityId));
            manager.sendServerPacket(target, destroy);

            PacketContainer remove =
                    manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            remove.getPlayerInfoAction().write(0,
                    EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            remove.getPlayerInfoDataLists().write(0, Collections.emptyList());
            manager.sendServerPacket(target, remove);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
