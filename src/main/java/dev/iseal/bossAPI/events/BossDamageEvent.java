package dev.iseal.bossAPI.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BossDamageEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private static boolean CANCELLED = false;
    private final double damage;
    private final Entity damager;
    private final Entity damaged;

    public BossDamageEvent(double damage, Entity damager, Entity damaged) {
        this.damage = damage;
        this.damager = damager;
        this.damaged = damaged;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public double getDamage() {
        return damage;
    }

    public Entity getDamager() {
        return damager;
    }

    public Entity getDamaged() {
        return damaged;
    }

    @Override
    public boolean isCancelled() {
        return CANCELLED;
    }

    @Override
    public void setCancelled(boolean cancel) {
        CANCELLED = cancel;
    }
}
