package dev.iseal.bossAPI.tasks;

import dev.iseal.bossAPI.systems.BossManager;
import dev.iseal.bossAPI.systems.phases.PhaseManager;
import org.bukkit.scheduler.BukkitRunnable;

public class RegenerateHealthTask extends BukkitRunnable {

    private final BossManager bossManager = BossManager.getInstance();
    private final PhaseManager phaseManager = PhaseManager.getInstance();

    @Override
    public void run() {
        bossManager.setBossHealth(phaseManager.getNewHealth(bossManager.getBossHealth()));
    }
}
