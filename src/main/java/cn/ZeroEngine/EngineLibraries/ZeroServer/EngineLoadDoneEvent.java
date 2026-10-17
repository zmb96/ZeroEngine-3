package cn.ZeroEngine.EngineLibraries.ZeroServer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF;
import cn.ZeroEngine.Engine.api.v3.SFApi;

public class EngineLoadDoneEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private static final long serialVersionUID = 1L;

    private final ZeroSF engineCore;
    private final SFApi engineApi;

    public EngineLoadDoneEvent(ZeroSF engineCore, SFApi engineApi) {
        this.engineCore = engineCore;
        this.engineApi = engineApi;
    }

    public ZeroSF getEngineCore() {
        return engineCore;
    }

    public SFApi getEngineApi() {
        return engineApi;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}