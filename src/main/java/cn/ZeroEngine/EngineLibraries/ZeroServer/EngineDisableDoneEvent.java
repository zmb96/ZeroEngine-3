package cn.ZeroEngine.EngineLibraries.ZeroServer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class EngineDisableDoneEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final long serialVersionUID = 1L;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}