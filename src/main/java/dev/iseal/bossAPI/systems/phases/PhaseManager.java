package dev.iseal.bossAPI.systems.phases;

import dev.iseal.bossAPI.BossAPI;
import dev.iseal.bossAPI.events.PhaseSwitchEvent;
import dev.iseal.bossAPI.misc.abstracts.AbstractPhaseClass;
import dev.iseal.bossAPI.systems.BossManager;
import dev.iseal.bossAPI.systems.attacks.AttackManager;
import dev.iseal.bossAPI.tasks.RegenerateHealthTask;
import dev.iseal.sealLib.Interfaces.Dumpable;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class PhaseManager implements Dumpable {

    private static PhaseManager instance;
    public static PhaseManager getInstance() {
        if (instance == null) {
            instance = new PhaseManager();
        }
        return instance;
    }

    //protect from instantiation
    protected PhaseManager() {}

    private int phaseIndex = 0;
    private String phaseName = "Phase 1";
    private AbstractPhaseClass currentPhase;
    private RegenerateHealthTask regenerateHealthTask;

    private final ArrayList<AbstractPhaseClass> phaseInstances = new ArrayList<>();

    public boolean checkPhaseChange(double health, Location location) {
        // check if its in current phase
        if (currentPhase.isInRange(health)) {
            return false;
        }
        // check if its in currentphase+1, and if it exists
        if (phaseIndex + 1 < phaseInstances.size() && phaseInstances.get(phaseIndex + 1).isInRange(health)) {
            setPhase(phaseIndex + 1, location);
            return true;
        }
        // check if its in currentphase-1, and if it exists
        if (phaseIndex - 1 >= 0 && phaseInstances.get(phaseIndex - 1).isInRange(health)) {
            setPhase(phaseIndex - 1, location);
            return true;
        }
        // wait what? - keep old phase
        ExceptionHandler.getInstance().dealWithException(new IllegalStateException("Health is not in any phase"), Level.WARNING, "HEALTH_NOT_IN_ANY_PHASE", "health: "+health);
        return false;
    }

    public void setPhase(int index, Location location) {
        if (index < 0 || index >= phaseInstances.size()) {
            ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Invalid phase index"), Level.WARNING, "SET_PHASE_WITH_INVALID_INDEX", "index: "+index);
        }
        PhaseSwitchEvent event = new PhaseSwitchEvent(phaseInstances.get(phaseIndex), phaseInstances.get(index));
        Bukkit.getPluginManager().callEvent(event);

        Entity boss = BossManager.getInstance().getBossEntity();
        Player bossPlayer = null;
        if (boss != null && boss instanceof Player player) {
            bossPlayer = player;
        }

        if (currentPhase != null) {
            if (currentPhase.hasSong())
                currentPhase.stopSong();

            if (currentPhase.canFly() && bossPlayer != null) {
                bossPlayer.setAllowFlight(false);
                bossPlayer.setFlying(false);
            }
        }

        phaseIndex = index;
        phaseName = phaseInstances.get(index).getPhaseName();
        currentPhase = phaseInstances.get(index);

        if (currentPhase.hasSong() && !currentPhase.isSongLetargic())
            // start song with location, again, only used for world basically
            // kind of bad practice, but it works
            currentPhase.startSong(location);

        if (currentPhase.canFly() && bossPlayer != null) {
            bossPlayer.setAllowFlight(true);
            bossPlayer.setFlying(true);
        }

        if (currentPhase.doesRegenerate() && !regenerateHealthTask.isCancelled()) {
            if (regenerateHealthTask != null) {
                regenerateHealthTask.cancel();
            }

            regenerateHealthTask = new RegenerateHealthTask();
            regenerateHealthTask.runTaskTimer(BossAPI.getPlugin(), 0, currentPhase.getRegenSpeed());
        } else if (regenerateHealthTask != null) {
            regenerateHealthTask.cancel();
            regenerateHealthTask = null;
        }

        AttackManager.getInstance().getAttacks().forEach(attack -> {
            if (attack.canBeDone(index)) {
                attack.setDamage(currentPhase.getActualDamage(attack.getDefaultDamage()));
                attack.setSpeed(currentPhase.getActualSpeed(attack.getDefaultSpeed()));
                attack.setSpeed(currentPhase.getActualSpeed(attack.getDefaultSpeed()));
            }
        });

        BossManager bossManager = BossManager.getInstance();
        bossManager.setBossBarColor(currentPhase.getBossBarColor());
        BukkitRunnable phaseSpecificTask = bossManager.getPhaseSpecificTask(phaseIndex);
        if (phaseSpecificTask != null) {
            phaseSpecificTask.runTaskTimer(BossAPI.getPlugin(), 0, 1);
        }
        bossManager.getPhaseSwitchTasks().forEach(task -> task.runTaskTimer(BossAPI.getPlugin(), 0, 1));
    }

    public boolean isResetOnEndPhase() {
        return currentPhase.isResetOnEndPhase();
    }

    public void addPhaseInstance(AbstractPhaseClass phaseInstance) {
        phaseInstances.add(phaseInstance);
    }

    public AbstractPhaseClass getCurrentPhase() {
        return currentPhase;
    }

    public int getCurrentPhaseNumber() {
        return phaseIndex;
    }

    public double getNewHealth(double health) {
        return currentPhase.getNewHealth(health);
    }

    public void invalidate() {
        phaseInstances.clear();
        phaseIndex = 0;
        phaseName = "Phase 1";
        currentPhase = null;
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> dump = new HashMap<>();
        dump.put("phaseIndex", phaseIndex);
        dump.put("phaseName", phaseName);
        dump.put("currentPhase", currentPhase.getClass().getName());
        dump.put("phaseInstances", phaseInstances);
        return dump;
    }
}
