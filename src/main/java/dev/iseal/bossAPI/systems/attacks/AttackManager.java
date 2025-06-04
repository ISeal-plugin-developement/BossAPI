package dev.iseal.bossAPI.systems.attacks;

import dev.iseal.bossAPI.misc.abstracts.AbstractAttackClass;
import dev.iseal.bossAPI.systems.ItemManager;
import dev.iseal.bossAPI.systems.phases.PhaseManager;
import dev.iseal.sealUtils.Interfaces.Dumpable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class AttackManager implements Dumpable {

    private static AttackManager instance;
    public static AttackManager getInstance() {
        if (instance == null) {
            instance = new AttackManager();
        }
        return instance;
    }

    private Logger logger = Bukkit.getLogger();
    private boolean isPlayerController = false;
    private HashMap<String, AbstractAttackClass> attacks = new HashMap<>();
    private PhaseManager phaseManager = PhaseManager.getInstance();

    public void invalidate() {
        logger = Bukkit.getLogger();
        isPlayerController = false;
        attacks.clear();
    }

    public void init(Entity boss) {
        if (boss instanceof Player plr) {
            isPlayerController = true;
            ItemManager.getInstance().registerItems();
            ItemManager.getInstance().getItems().forEach(item -> plr.getInventory().addItem(item));
        }
    }

    public void registerAttack(AbstractAttackClass attack) {
        if (attacks.containsKey(attack.getAttackName())) {
            logger.warning("Attack with name " + attack.getAttackName() + " already exists! Replacing...");
        }
        attacks.put(attack.getAttackName(), attack);
    }

    public ArrayList<AbstractAttackClass> getAttacks() {
        return new ArrayList<>(attacks.values());
    }

    public boolean isPlayerController() {
        return isPlayerController;
    }

    public boolean attemptAttack(AbstractAttackClass attackClass, Entity optionalTarget) {
        if (attackClass.isTargeted() && optionalTarget != null) {
            if (!attackClass.canExecute(phaseManager.getCurrentPhaseNumber(), optionalTarget)) {
                return false;
            }
            attackClass.attack(optionalTarget);
        } else {
            if (!attackClass.canExecute(phaseManager.getCurrentPhaseNumber())) {
                return false;
            }
            attackClass.attack();
        }
        attackClass.setLastExecution();
        return true;
    }

    public AbstractAttackClass getAttackByName(String name) {
        if (!attacks.containsKey(name)) {
            logger.warning("Attack with name " + name + " does not exist!");
            return null;
        }
        return attacks.get(name);
    }

    @Override
    public HashMap<String, Object> dump() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("isPlayerController", isPlayerController);
        data.put("attacks", attacks);
        return data;
    }
}
