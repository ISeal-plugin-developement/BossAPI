package dev.iseal.bossAPI.misc.abstracts;

import dev.iseal.bossAPI.systems.BossManager;
import dev.iseal.sealLib.Helpers.NSKeyHelper;
import dev.iseal.sealLib.Helpers.SoundHelper;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public abstract class AbstractAttackClass {

    private final String name;
    private final double defaultDamage;
    private final double defaultSpeed;
    private final double defaultRange;
    private final double defaultMaxCooldown;
    private double damage;
    private double speed;
    private double range;
    private double maxCooldown;
    private final int minPhase;
    private final int maxPhase;
    private final boolean isInvulnerableDuringAttack;
    private final boolean isTargeted;
    private final ItemStack attackItem;
    private final BossManager bossManager = BossManager.getInstance();
    private final String attackSound;
    private final String attackSoundNamespace;

    private long lastExecution = 0;
    private boolean hasSetItemUp = false;

    public AbstractAttackClass(String name, double defaultDamage, double defaultSpeed, double defaultRange, double cooldown, int minPhase, int maxPhase, boolean isInvulnerableDuringAttack, boolean isTargeted, ItemStack attackItem, String attackSound, String attackSoundNamespace) {
        this.name = name;
        this.defaultDamage = defaultDamage;
        this.defaultSpeed = defaultSpeed;
        this.defaultRange = defaultRange;
        this.defaultMaxCooldown = cooldown;
        this.minPhase = minPhase;
        this.maxPhase = maxPhase;
        this.isInvulnerableDuringAttack = isInvulnerableDuringAttack;
        this.isTargeted = isTargeted;
        this.attackSound = attackSound;
        this.attackSoundNamespace = attackSoundNamespace;
        this.attackItem = attackItem;


    }

    public String getAttackName() {
        return name;
    }

    public double getDefaultDamage() {
        return defaultDamage;
    }

    public double getDefaultSpeed() {
        return defaultSpeed;
    }

    public double getDefaultRange() {
        return defaultRange;
    }

    public double geDefaultCooldown() {
        return defaultMaxCooldown;
    }

    public void setCooldown(double cooldown) {
        this.maxCooldown = cooldown;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCooldown() {
        return maxCooldown;
    }

    public double getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRange() {
        return range;
    }

    public boolean isInvulnerableDuringAttack() {
        return isInvulnerableDuringAttack;
    }

    public boolean canBeDone(int phase) {
        return phase >= minPhase && phase <= maxPhase;
    }

    public boolean canExecute(int phase, Entity tryingToAttack) {
        return System.currentTimeMillis() >= lastExecution + defaultMaxCooldown
                && canBeDone(phase)
                && bossManager.getBossEntity().getLocation().distance(tryingToAttack.getLocation()) <= defaultRange
                && !bossManager.isAttacking();
    }

    public boolean canExecute(int phase) {
        return System.currentTimeMillis() >= lastExecution + defaultMaxCooldown
                && canBeDone(phase)
                && !bossManager.isAttacking();
    }

    public void setLastExecution() {
        lastExecution = System.currentTimeMillis();
    }

    public boolean isTargeted() {
        return isTargeted;
    }

    public ItemStack getAttackItem() {
        if (!hasSetItemUp) {
            // make item connected to this attack
            ItemMeta meta = attackItem.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            pdc.set(NSKeyHelper.getKey("is_attack_item"), PersistentDataType.BOOLEAN, true);
            pdc.set(NSKeyHelper.getKey("attack_name"), PersistentDataType.STRING, name);
            attackItem.setItemMeta(meta);
            hasSetItemUp = true;
        }
        return attackItem;
    }

    public void attack(Entity target) {
        SoundHelper.playSoundAroundEntity(attackSoundNamespace, attackSound, 1f, bossManager.getBossEntity());
        actuallyAttack(target);
    }

    public void attack() {
        SoundHelper.playSoundAroundEntity(attackSoundNamespace, attackSound, 1f, bossManager.getBossEntity());
        actuallyAttack();
    }

    public abstract void actuallyAttack(Entity target);
    public abstract void actuallyAttack();

}
