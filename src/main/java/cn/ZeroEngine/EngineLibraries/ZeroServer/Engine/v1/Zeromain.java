package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v1;

import cn.ZeroEngine.Engine.api.v3.SFApi;
import cn.ZeroEngine.EngineLibraries.ZeroServer.EngineDisableDoneEvent;
import cn.ZeroEngine.EngineLibraries.ZeroServer.EngineLoadDoneEvent;
import cn.ZeroEngine.EngineLibraries.ZeroServer.json;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF;
import cn.ZeroEngine.Engine.api.v3.database.DatabaseManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroLifestealEnchant;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroAncestralMightEnchant;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroEnchantManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.chat.ZeroChatCommand;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.chat.ZeroChatManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.main.ZeroReachCommand;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.perf.ZeroPerformanceCommand;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.permission.ZeroPermissionCommand;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.permission.ZeroPermissionManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.world.ZeroWorldCommand;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.world.ZeroWorldManager;
import cn.ZeroEngine.Engine.api.v3.feature.teleport.TeleportManager;
import cn.ZeroEngine.Engine.api.v3.database.Database;

@Deprecated
public final class Zeromain extends JavaPlugin {

    private static Economy eco;
    private TeleportManager teleportManager;

    public static final String ZeroEngine_VERSION = "3.5.16";
    public static final String ZeroEngine_GunMu = null;

    @Deprecated
    @Override
    public void onEnable() {
        ZeroSF.init(this);
        ZeroSF sf = ZeroSF.sf();

        json lang = new json(this, "lang.json").load();

        sf.info(lang.getStringPath("info.Zeromain-onEnable.line38"));
        saveDefaultConfig();

        play();

        boolean dbOk = DatabaseManager.init(this);
        sf.info(lang.getStringPath("info.Zeromain-onEnable.line42") + dbOk);

        teleportManager = new TeleportManager(this);
        sf.setTeleportManager(teleportManager);

        sf
                .regCommand("Zeroservermanagement", new Zeroservermanagement(this))
                .regCommand("ru", new Zerorulescom())
                .regCommand("sh", new Zerohelpcom())
                .regCommand("ty", new Zerotycon(this))
                .regCommand("Zerogiveit", new Zerogiveit(this));

        ZeroEnchantManager em = sf.enchant();

        ZeroChatManager chatManager = sf.chat();
        sf.regCommand("sfchat", new ZeroChatCommand(chatManager));

        ZeroWorldManager worldManager = sf.world();
        sf.regCommand("sfworld", new ZeroWorldCommand(worldManager));

        ZeroPermissionManager permManager = sf.permission();
        sf.regCommand("sfperm", new ZeroPermissionCommand(permManager));

        sf.reach();
        sf.regCommand("sfreach", new ZeroReachCommand(sf.reach()));

        sf.perf();
        sf.regCommand("sfperf", new ZeroPerformanceCommand(sf.perf()));

        cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF.init(this);
        cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF v3sf = cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF.sf();
        v3sf.enchant();
        v3sf.item();

        sf.info(lang.getStringPath("info.Zeromain-onEnable.line87"));
        sf.info(lang.getStringPath("info.Zeromain-onEnable.line88-1") + sf.eco().ready() + lang.getStringPath("info.Zeromain-onEnable.line88-2") + sf.eco().hasEssentials() + lang.getStringPath("info.Zeromain-onEnable.line88-3") + sf.eco().hasVault() + lang.getStringPath("info.Zeromain-onEnable.line88-4"));
        Bukkit.getPluginManager().callEvent(
                new EngineLoadDoneEvent(ZeroSF.sf(), SFApi.get())
        );
    }

    @Deprecated
    @Override
    public void onDisable() {
        Bukkit.getPluginManager().callEvent(
                new EngineDisableDoneEvent()
        );
        ZeroSF sf = null;
        json lang = new json(this, "lang.json").load();
        try { sf = ZeroSF.sf(); } catch (IllegalStateException ignored) {}
        if (sf == null) {
            getLogger().warning(lang.getStringPath("info.Zeromain-onDisable.line98"));
            return;
        }
        sf.info(lang.getStringPath("info.Zeromain-onDisable.line101"));

        DatabaseManager.shutdown();
        ZeroSF.shutdown();
    }

    @Deprecated
    private boolean setupEconomy() {
        try {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                return false;
            }
            RegisteredServiceProvider<Economy> rsp =
                    getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            eco = rsp.getProvider();
            return eco != null;
        } catch(NullPointerException e) {

        }
        return eco != null;
    }

    @Deprecated
    public static Economy eco() {
        return eco;
    }

    @Deprecated
    private void play() {
        ZeroSF sf = ZeroSF.sf();
        json lang = new json(this, "lang.json").load();
        for (int i = 1;i <= 9;i++) {
            sf.info(lang.getStringPath("Zeromain-play.line"+i));
        }
    }

    @Deprecated
    public static ZeroSF sf() {
        return ZeroSF.sf();
    }
}
