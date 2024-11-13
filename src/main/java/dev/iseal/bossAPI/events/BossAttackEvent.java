package dev.iseal.bossAPI.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BossAttackEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private static boolean CANCELLED = false;

    public BossAttackEvent() {
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
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
