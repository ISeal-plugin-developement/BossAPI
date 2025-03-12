package dev.iseal.bossAPI.systems;

import dev.iseal.bossAPI.BossAPI;
import dev.iseal.bossAPI.events.BossDeathEvent;
import dev.iseal.bossAPI.events.StartBossFightEvent;
import dev.iseal.bossAPI.misc.abstracts.AbstractBossClass;
import dev.iseal.bossAPI.systems.attacks.AttackManager;
import dev.iseal.bossAPI.systems.phases.PhaseManager;
import dev.iseal.sealLib.Interfaces.Dumpable;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class BossManager implements Dumpable {

    private static BossManager instance;
    public static BossManager getInstance() {
        if (instance == null) {
            instance = new BossManager();
        }
        return instance;
    }

    //protect from instantiation
    protected BossManager() {
        dumpableInit();
    }

    private AbstractBossClass bossClass;
    private boolean isAttacking = false;
    private boolean isFighting = false;
    private ArrayList<BukkitRunnable> persistentTasks = new ArrayList<>();
    private ArrayList<BukkitRunnable> phaseSwitchTasks = new ArrayList<>();
    private HashMap<Integer, BukkitRunnable> phaseSpecificTasks = new HashMap<>();
    private final AttackManager attackManager = AttackManager.getInstance();
    private final PhaseManager phaseManager = PhaseManager.getInstance();
    private BossBar bossBar;

    /**
        * Check if the entity is the boss
        * @param entity the entity to check
        * @return true if the entity is the boss
     */
    public boolean isBoss(Entity entity) {
        if (bossClass == null)
            return false;
        return entity.equals(bossClass.getBossEntity());
    }

    /**
        * Start the boss fight
        * @param boss the boss entity
        * @throws IllegalStateException if the boss is already fighting
     */
    public void startBossFight(AbstractBossClass boss) {
        if (isFighting) {
            ExceptionHandler.getInstance().dealWithException(new IllegalStateException("Boss is already fighting"), Level.WARNING, "BOSS_ALREADY_FIGHTING", "new boss: "+boss, "old boss: "+this.bossClass);
            return;
        }
        isFighting = true;
        this.bossClass = boss;
        bossBar = Bukkit.createBossBar(boss.getBossName(), BarColor.RED, boss.getBossBarStyle());
        Bukkit.getServer().getOnlinePlayers().forEach(player -> bossBar.addPlayer(player));
        persistentTasks.forEach(task -> task.runTaskTimer(BossAPI.getPlugin(), 0, 1));
        phaseManager.setPhase(0, boss.getBossEntity().getLocation());
        bossBar.setColor(phaseManager.getCurrentPhase().getBossBarColor());
        AttackManager.getInstance().init(boss.getBossEntity());
        StartBossFightEvent event = new StartBossFightEvent(boss.getBossEntity());
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
        * Set the boss entity
        * @param boss the boss entity
        *
        * WARNING: This method is under certain circumstances unsafe! Use startBossFight(Entity boss) instead!
     */
    protected void setBoss(AbstractBossClass boss) {
        this.bossClass = boss;
    }

    /**
        * Get the boss entity
        * @return the boss entity
     */
    public Entity getBossEntity() {
        if (bossClass == null) {
            return null;
        }
        return bossClass.getBossEntity();
    }

    /**
    * Damages the boss
    * @param damage the damage to deal
    * @throws IllegalStateException if the boss is not fighting or the input is invalid
     */
    public void damageBoss(double damage) {
        if (!isFighting) {
            ExceptionHandler.getInstance().dealWithException(new IllegalStateException("Boss is not fighting"), Level.WARNING, "BOSS_NOT_FIGHTING_WHEN_ATTACKED", "boss: " + bossClass);
            return;
        }
        if (damage < 0) {
            ExceptionHandler.getInstance().dealWithException(new IllegalArgumentException("Damage cannot be negative"), Level.WARNING, "NEGATIVE_DAMAGE", "damage: " + damage);
            return;
        }
        bossClass.removeBossHealth(damage);
        double health = bossClass.getBossHealth();
        if (health <= 0) {
            killBoss();
            return;
        }
        onHealthChange(health);
    }

    /**
    * Kills the boss and resets the fight
     */
    public void killBoss() {
        isFighting = false;
        isAttacking = false;
        BossDeathEvent event = new BossDeathEvent(bossClass.getBossEntity());
        Bukkit.getPluginManager().callEvent(event);
        bossClass = null;
        phaseManager.invalidate();
        attackManager.invalidate();
        instance = null;
        bossBar.removeAll();
    }

    /**
        * check if the boss is currently doing an attack (implies fighting)
        * @return true if the boss is attacking
     */
    public boolean isAttacking() {
        return isAttacking;
    }

    /**
     * check if there is a fight going on (doesn't mean the boss is attacking)
     * @return true if the boss is fighting
     */
    public boolean isFighting() {
        return isFighting;
    }


    public void addPlayerBar(Player player) {
        if (isFighting && bossBar != null) {
            bossBar.addPlayer(player);
        }
    }

    /**
    * Get the phase switch tasks
    * @return the phase switch tasks
     */
    public ArrayList<BukkitRunnable> getPhaseSwitchTasks() {
        return phaseSwitchTasks;
    }

    /**
        * Get a phase specific task
        * @return the phase specific task
     */
    public BukkitRunnable getPhaseSpecificTask(int phase) {
        return phaseSpecificTasks.get(phase);
    }

    /**
        * Set the boss bar color
     */
    public void setBossBarColor(BarColor bossBarColor) {
        bossBar.setColor(bossBarColor);
    }

    /**
        * Get the boss health
        * @return the boss health
     */
    public double getBossHealth() {
        return bossClass.getBossHealth();
    }

    /**
        * Set the boss health and update the boss bar
        * @param newHealth the new health
     */
    public void setBossHealth(double newHealth) {
        bossClass.setBossHealth(newHealth);
        onHealthChange(newHealth);
    }

    /**
        * Do the necessary calculations and set the new hp
     * @param health the new health
     */
    private void onHealthChange(double health) {
        phaseManager.checkPhaseChange(health, bossClass.getBossEntity().getLocation());
        double minHealth = phaseManager.getCurrentPhase().getMinHealth();
        double maxHealth = phaseManager.getCurrentPhase().getMaxHealth();
        double phaseHealth = health - minHealth;
        double phaseMaxHealth = maxHealth - minHealth;
        if (phaseManager.isResetOnEndPhase()) {
            bossBar.setProgress(phaseHealth / phaseMaxHealth);
        } else {
            bossBar.setProgress(health / bossClass.getMaxHealth());
        }
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> dump = new HashMap<>();
        dump.put("boss", bossClass);
        dump.put("isAttacking", isAttacking);
        dump.put("isFighting", isFighting);
        dump.put("phaseManager", phaseManager);
        dump.put("phaseSwitchTasks", phaseSwitchTasks);
        dump.put("persistentTasks", persistentTasks);
        dump.put("phaseSpecificTasks", phaseSpecificTasks);
        return dump;
    }
}
