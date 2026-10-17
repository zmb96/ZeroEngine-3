package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.gui;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF;
import cn.ZeroEngine.Engine.api.v3.feature.gui.ChestGUI;
import cn.ZeroEngine.Engine.api.v3.feature.gui.GUIManager;
import cn.ZeroEngine.Engine.api.v3.feature.gui.SChestGUI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ZeroGUIManagerImpl implements GUIManager {

    private final Plugin plugin;
    private final PluginManager pm;
    private final Set<ZeroChestGUIImpl> activeGUIs = new CopyOnWriteArraySet<>();
    private final Map<String, SChestGUI> byCommand = new HashMap<>();

    public ZeroGUIManagerImpl(Plugin plugin) {
        this.plugin = plugin;
        this.pm = plugin.getServer().getPluginManager();
    }

    @Override
    public ChestGUI create() {
        return create("GUI", 3, true);
    }

    @Override
    public ChestGUI create(String title, int rows) {
        return create(title, rows, true);
    }

    @Override
    public ChestGUI create(String title, int rows, boolean readonly) {
        ZeroChestGUIImpl gui = new ZeroChestGUIImpl(plugin);
        gui.title(title);
        gui.rows(rows);
        gui.readonly(readonly);
        pm.registerEvents(gui, plugin);
        activeGUIs.add(gui);
        return gui;
    }

    @Override
    public void register(SChestGUI gui) {
        String cmd = gui.command();
        if (cmd == null || cmd.isEmpty()) return;
        byCommand.put(cmd.toLowerCase(), gui);
        ZeroSF.sf().regCommand(cmd, new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
                if (!(s instanceof Player p)) { s.sendMessage("§c仅玩家可用"); return true; }
                gui.open(p);
                return true;
            }
        });
        ZeroSF.sf().info("[GUI] SChestGUI registered: " + gui.id() + " (command: /" + cmd + ")");
    }

    @Override
    public void unregister(SChestGUI gui) {
        String cmd = gui.command();
        if (cmd != null && !cmd.isEmpty()) {
            SChestGUI cur = byCommand.get(cmd.toLowerCase());
            if (cur == gui) byCommand.remove(cmd.toLowerCase());
        }
        byCommand.values().removeIf(v -> v == gui);
        ZeroSF.sf().info("[GUI] SChestGUI unregistered: " + gui.id());
    }

    @Override
    public SChestGUI byCommand(String command) {
        return command == null ? null : byCommand.get(command.toLowerCase());
    }

    @Override
    public void closeAll() {
        for (ZeroChestGUIImpl gui : activeGUIs) {
            try { gui.unregister(); } catch (Exception ignored) {}
        }
        activeGUIs.clear();
        byCommand.clear();
    }
}
