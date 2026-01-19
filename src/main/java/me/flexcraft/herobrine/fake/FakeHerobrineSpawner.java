package me.flexcraft.herobrine.fake;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

public class FakeHerobrineSpawner {

    public static void spawn(Player target) {
        World world = target.getWorld();

        // 📍 Спавн ПРЯМО ПЕРЕД ИГРОКОМ
        Location loc = target.getLocation().clone()
                .add(target.getLocation().getDirection().normalize().multiply(3));

        // 👁️ Создаём "Херобрина"
        Villager herobrine = world.spawn(loc, Villager.class, v -> {
            v.setCustomName("§fHerobrine");
            v.setCustomNameVisible(true);
            v.setAI(false);
            v.setInvulnerable(true);
            v.setSilent(true);
            v.setCollidable(false);
        });

        // 😈 ПОВОРОТ ГОЛОВЫ К ИГРОКУ
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!herobrine.isValid() || !target.isOnline()) {
                    herobrine.remove();
                    cancel();
                    return;
                }

                herobrine.teleport(herobrine.getLocation().setDirection(
                        target.getLocation().toVector()
                                .subtract(herobrine.getLocation().toVector())
                ));

                ticks++;
                if (ticks >= 60) { // ~3 секунды
                    herobrine.remove();
                    cancel();
                }
            }
        }.runTaskTimer(
                org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(FakeHerobrineSpawner.class),
                0L, 1L
        );
    }
}
