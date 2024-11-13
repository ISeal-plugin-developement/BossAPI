package dev.iseal.bossAPI.events;

import dev.iseal.bossAPI.misc.abstracts.AbstractPhaseClass;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PhaseSwitchEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AbstractPhaseClass fromClass;
    private final AbstractPhaseClass toClass;

    public PhaseSwitchEvent(AbstractPhaseClass fromClass, AbstractPhaseClass toClass) {
        this.fromClass = fromClass;
        this.toClass = toClass;
    }

    public AbstractPhaseClass getFromClass() {
        return fromClass;
    }

    public AbstractPhaseClass getToClass() {
        return toClass;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
