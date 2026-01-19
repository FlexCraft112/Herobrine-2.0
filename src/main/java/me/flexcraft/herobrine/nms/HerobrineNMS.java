package me.flexcraft.herobrine.nms;

import com.mojang.authlib.GameProfile;
import me.flexcraft.herobrine.HerobrinePlugin;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.UUID;

public class HerobrineNMS {

    public static void spawn(Player target, HerobrinePlugin plugin) {

        // === NMS доступ ===
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        MinecraftServer mcServer = craftServer.getServer();
        ServerLevel level = ((CraftWorld) target.getWorld()).getHandle();

        // === Fake Player ===
        GameProfile profile = new GameProfile(UUID.randomUUID(), "Herobrine");
        ServerPlayer herobrine = new ServerPlayer(mcServer, level, profile);
        herobrine.setGameMode(GameType.SURVIVAL);
        herobrine.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);

        // === Позиция перед игроком ===
        Location loc = target.getLocation();
        Location front = loc.clone().add(
                loc.getDirection().normalize()
                        .multiply(plugin.getConfig().getDouble("herobrine.spawn-distance"))
        );
        herobrine.setPos(front.getX(), front.getY(), front.getZ());

        CraftPlayer craftTarget = (CraftPlayer) target;

        // === Показать Херобрина только цели ===
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

        // === Тьма ===
        if (plugin.getConfig().getBoolean("herobrine.effects.darkness")) {
            target.addPotionEffect(new PotionEffect(
                    PotionEffectType.DARKNESS,
                    plugin.getConfig().getInt("herobrine.effects.darkness-duration"),
                    0
            ));
        }

        // === Слежение взглядом ===
        BukkitTask lookTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Location t = target.getLocation();
            double dx = t.getX() - herobrine.getX();
            double dz = t.getZ() - herobrine.getZ();
            double dy = (t.getY() + 1.6) - herobrine.getY();

            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

            herobrine.setYRot(yaw);
            herobrine.setXRot(pitch);
            herobrine.yHeadRot = yaw;
        }, 0L, 1L);

        // === Таймер ===
        int durationTicks = plugin.getConfig().getInt("herobrine.duration-seconds") * 20;

        // === Последний тик: резкий взгляд + шаг + УДАР-ПРИЗРАК ===
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            // Резкий взгляд
            Location t = target.getLocation();
            double dx = t.getX() - herobrine.getX();
            double dz = t.getZ() - herobrine.getZ();
            double dy = (t.getY() + 1.6) - herobrine.getY();

            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

            herobrine.setYRot(yaw);
            herobrine.setXRot(pitch);
            herobrine.yHeadRot = yaw;

            // Микро-шаг вперёд
            Vector step = t.toVector().subtract(
                    new Vector(herobrine.getX(), herobrine.getY(), herobrine.getZ())
            ).normalize().multiply(0.35);
            herobrine.setPos(
                    herobrine.getX() + step.getX(),
                    herobrine.getY(),
                    herobrine.getZ() + step.getZ()
            );

            // 👊 УДАР-ПРИЗРАК
            if (plugin.getConfig().getBoolean("herobrine.ghost-hit.enabled")) {

                double dmg = plugin.getConfig().getDouble("herobrine.ghost-hit.damage");
                if (dmg > 0) {
                    target.damage(dmg);
                }

                double kb = plugin.getConfig().getDouble("herobrine.ghost-hit.knockback");
                Vector knock = target.getLocation().toVector()
                        .subtract(new Vector(herobrine.getX(), herobrine.getY(), herobrine.getZ()))
                        .normalize().multiply(kb).setY(0.2);
                target.setVelocity(knock);

                if (plugin.getConfig().getBoolean("herobrine.ghost-hit.confusion")) {
                    target.addPotionEffect(new PotionEffect(
                            PotionEffectType.CONFUSION,
                            plugin.getConfig().getInt("herobrine.ghost-hit.confusion-duration"),
                            0
                    ));
                }
            }

            // Через 1 тик — исчезновение
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                lookTask.cancel();
                craftTarget.getHandle().connection.send(
                        new ClientboundPlayerInfoUpdatePacket(
                                ClientboundPlayerInfoUpdatePacket.Action.REMOVE_PLAYER,
                                herobrine
                        )
                );
            }, 1L);

        }, durationTicks - 1L);
    }
}
