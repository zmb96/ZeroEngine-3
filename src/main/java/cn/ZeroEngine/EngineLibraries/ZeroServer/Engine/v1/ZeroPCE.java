package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v1;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Deprecated
public class ZeroPCE implements Listener {
    private final Zeromain plugin;

    public ZeroPCE(Zeromain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void ce(AsyncPlayerChatEvent e) {

    }
}
