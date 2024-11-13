package dev.iseal.bossAPI.misc.abstracts;

import org.bukkit.Material;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractBossClass {

    private final String name;
    private final double maxHealth;
    private final Entity bossEntity;
    private final BarStyle bossBarStyle;

    private double health;

    public AbstractBossClass(String name, double maxHealth, Entity bossEntity, BarStyle bossBarStyle) {
        this.name = name;
        this.maxHealth = maxHealth;
        health = maxHealth;
        this.bossEntity = bossEntity;
        this.bossBarStyle = bossBarStyle;
    }

    public void setBossHealth(double health) {
        this.health = health;
        new ItemStack(Material.ACACIA_FENCE).serialize();
        ItemStack.deserialize(new ItemStack(Material.ACACIA_FENCE).serialize());
    }

    public void removeBossHealth(double health) {
        this.health -= health;
    }

    public void addBossHealth(double health) {
        this.health += health;
    }

    public double getBossHealth() {
        return health;
    }

    public String getBossName() {
        return name;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public Entity getBossEntity() {
        return bossEntity;
    }

    public BarStyle getBossBarStyle() {
        return bossBarStyle;
    }
}
