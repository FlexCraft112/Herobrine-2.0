package me.flexcraft.herobrine.fake;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.UUID;

public class FakeHerobrineSpawner {

    private static final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

    public static void spawn(JavaPlugin plugin, Player target) {
        try {
            // 🔹 UUID и профиль (скин Херобрина)
            UUID uuid = UUID.randomUUID();
            WrappedGameProfile profile = new WrappedGameProfile(uuid, "Herobrine");

            // ⚠ СКИН ХЕРОБРИНА (белые глаза)
            profile.getProperties().put("textures", new WrappedSignedProperty(
                    "textures",
                    "ewogICJ0aW1lc3RhbXAiIDogMTYxNjE2NDc2NjE2NiwKICAicHJvZmlsZUlkIiA6ICIyMTY4ODI1YzZmNDA0OTljOWE4Y2U5NzU3NzE3MzJkNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJIZXJvYnJpbmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q5YzZjM2Y3OTRmZmRjY2Y1MWM5NzJmYzY5ZGI3ODhjMjVjOTljYzMzNzg4ZTEzZDI3Y2FjZjY2NSIKICAgIH0KICB9Cn0=",
                    "signature"
            ));

            // 📍 Позиция ПЕРЕД игроком
            Location loc = target.getLocation().clone();
            loc.add(loc.getDirection().normalize().multiply(2));
            loc.setYaw(target.getLocation().getYaw() + 180);
            loc.setPitch(0);

            int entityId = (int) (Math.random() * Integer.MAX_VALUE);

            // 🔹 PLAYER_INFO (ADD)
            PacketContainer infoAdd = protocol.createPacket(PacketType.Play.Server.PLAYER_INFO);
            infoAdd.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            infoAdd.getPlayerInfoDataLists().write(0, Collections.singletonList(
                    new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText("Herobrine"))
            ));

            // 🔹 SPAWN ENTITY
            PacketContainer spawn = protocol.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            spawn.getIntegers().write(0, entityId);
            spawn.getUUIDs().write(0, uuid);
            spawn.getDoubles()
                    .write(0, loc.getX())
                    .write(1, loc.getY())
                    .write(2, loc.getZ());
            spawn.getBytes()
                    .write(0, (byte) (loc.getYaw() * 256 / 360))
                    .write(1, (byte) (loc.getPitch() * 256 / 360));

            // 🔹 Отправляем пакеты
            protocol.sendServerPacket(target, infoAdd);
            protocol.sendServerPacket(target, spawn);

            // 😨 ЭФФЕКТЫ
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 10));
            target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 0.5f);

            // ⏳ УДАЛЕНИЕ ЧЕРЕЗ 2 СЕК
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    PacketContainer destroy = protocol.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
                    destroy.getIntLists().write(0, Collections.singletonList(entityId));
                    protocol.sendServerPacket(target, destroy);
                } catch (Exception ignored) {}
            }, 40L);

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
