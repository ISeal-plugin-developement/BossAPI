package dev.iseal.bossAPI.events;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StartBossFightEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Entity bossEntity;

    public StartBossFightEvent(Entity bossEntity) {
        this.bossEntity = bossEntity;
    }

    public Entity getBossEntity() {
        return bossEntity;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
