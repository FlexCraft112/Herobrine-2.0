package me.flexcraft.herobrine.nms;

import com.mojang.authlib.GameProfile;
import me.flexcraft.herobrine.HerobrinePlugin;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HerobrineNMS {

    public static void spawn(Player target, HerobrinePlugin plugin) {

        // === Получаем NMS объекты ===
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        MinecraftServer mcServer = craftServer.getServer();
        ServerLevel level = ((CraftWorld) target.getWorld()).getHandle();

        // === Создаём фейкового игрока ===
        GameProfile profile = new GameProfile(UUID.randomUUID(), "Herobrine");
        ServerPlayer herobrine = new ServerPlayer(mcServer, level, profile);
        herobrine.setGameMode(GameType.SURVIVAL);

        // === Позиция перед игроком ===
        Location loc = target.getLocation();
        Location front = loc.clone().add(
                loc.getDirection()
                        .normalize()
                        .multiply(plugin.getConfig().getDouble("herobrine.spawn-distance"))
        );

        herobrine.setPos(front.getX(), front.getY(), front.getZ());
        herobrine.setYRot(loc.getYaw() + 180); // смотрит на игрока
        herobrine.setXRot(loc.getPitch());

        CraftPlayer craftTarget = (CraftPlayer) target;

        // === Показать Херобрина ТОЛЬКО цели ===
        craftTarget.getHandle().connection.send(
                new ClientboundPlayerInfoUpdatePacket(
                        ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        herobrine
                )
        );
        craftTarget.getHandle().connection.send(
                new ClientboundAddPlayerPacket(herobrine)
        );

        // === Звук ===
        if (plugin.getConfig().getBoolean("herobrine.play-sound")) {
            target.playSound(
                    target.getLocation(),
                    plugin.getConfig().getString("herobrine.sound"),
                    (float) plugin.getConfig().getDouble("herobrine.sound-volume"),
                    (float) plugin.getConfig().getDouble("herobrine.sound-pitch")
            );
        }

        // === Эффект тьмы ===
        if (plugin.getConfig().getBoolean("herobrine.effects.darkness")) {
            target.addPotionEffect(
                    new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.DARKNESS,
                            plugin.getConfig().getInt("herobrine.effects.darkness-duration"),
                            0
                    )
            );
        }

        // === Удаление через N секунд ===
        int durationTicks = plugin.getConfig().getInt("herobrine.duration-seconds") * 20;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            craftTarget.getHandle().connection.send(
                    new ClientboundPlayerInfoUpdatePacket(
                            ClientboundPlayerInfoUpdatePacket.Action.REMOVE_PLAYER,
                            herobrine
                    )
            );
        }, durationTicks);
    }
}
