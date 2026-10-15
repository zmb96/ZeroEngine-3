package cn.ZeroEngine.Engine.api.v1;

import cn.ZeroEngine.EngineLibraries.json;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import cn.ZeroEngine.Engine.api.v2.SF;
import cn.ZeroEngine.Engine.api.v2.database.DatabaseManager;
import cn.ZeroEngine.Engine.api.v2.feature.enchant.LifestealEnchant;
import cn.ZeroEngine.Engine.api.v2.feature.enchant.AncestralMightEnchant;
import cn.ZeroEngine.Engine.api.v2.feature.enchant.EnchantManager;
import cn.ZeroEngine.Engine.api.v2.feature.chat.ChatCommand;
import cn.ZeroEngine.Engine.api.v2.feature.chat.ChatManager;
import cn.ZeroEngine.Engine.api.v2.feature.main.ReachCommand;
import cn.ZeroEngine.Engine.api.v2.feature.perf.PerformanceCommand;
import cn.ZeroEngine.Engine.api.v2.feature.permission.PermissionCommand;
import cn.ZeroEngine.Engine.api.v2.feature.permission.PermissionManager;
import cn.ZeroEngine.Engine.api.v2.feature.world.WorldCommand;
import cn.ZeroEngine.Engine.api.v2.feature.world.WorldManager;
import cn.ZeroEngine.Engine.api.v2.feature.teleport.TeleportManager;

import java.io.File;

@Deprecated
public final class main extends JavaPlugin {

    private static Economy eco;
    private TeleportManager teleportManager;

    public static final String ZeroEngine_VERSION = "3.5.12";
    public static final String ZeroEngine_GunMu = null;

    @Deprecated
    @Override
    public void onEnable() {
        SF.init(this);
        SF sf = SF.sf();

        json lang = new json(this, "lang.json").load();

        sf.info(lang.getStringPath("info.main-onEnable.line38"));
        saveDefaultConfig();

        boolean dbOk = DatabaseManager.init(this);
        sf.info(lang.getStringPath("info.main-onEnable.line42") + dbOk);

        teleportManager = new TeleportManager(this);
        sf.setTeleportManager(teleportManager);

        sf
                .regCommand("servermanagement", new servermanagement(this))
                .regCommand("ru", new rulescom())
                .regCommand("sh", new helpcom())
                .regCommand("ty", new tycon(this))
                .regCommand("giveit", new giveit(this));

        EnchantManager em = sf.enchant();
        em.register(new LifestealEnchant());
        em.register(new AncestralMightEnchant());
        
        
        
        

        ChatManager chatManager = sf.chat();
        sf.regCommand("sfchat", new ChatCommand(chatManager));

        WorldManager worldManager = sf.world();
        sf.regCommand("sfworld", new WorldCommand(worldManager));

        PermissionManager permManager = sf.permission();
        sf.regCommand("sfperm", new PermissionCommand(permManager));

        sf.reach();
        sf.regCommand("sfreach", new ReachCommand(sf.reach()));

        sf.perf();
        sf.regCommand("sfperf", new PerformanceCommand(sf.perf()));

        
        
        
        
        
        cn.ZeroEngine.Engine.api.v3.SF.init(this);
        cn.ZeroEngine.Engine.api.v3.SF v3sf = cn.ZeroEngine.Engine.api.v3.SF.sf();
        v3sf.enchant();
        v3sf.item();

        sf.info(lang.getStringPath("info.main-onEnable.line87"));
        sf.info(lang.getStringPath("info.main-onEnable.line88-1") + sf.eco().ready() + lang.getStringPath("info.main-onEnable.line88-2") + sf.eco().hasEssentials() + lang.getStringPath("info.main-onEnable.line88-3") + sf.eco().hasVault() + lang.getStringPath("info.main-onEnable.line88-4"));
    }

    @Deprecated
    @Override
    public void onDisable() {
        SF sf = null;
        json lang = new json(this, "lang.json").load();
        try { sf = SF.sf(); } catch (IllegalStateException ignored) {}
        if (sf == null) {
            getLogger().warning(lang.getStringPath("info.main-onDisable.line98"));
            return;
        }
        sf.info(lang.getStringPath("info.main-onDisable.line101"));
        DatabaseManager.shutdown();
        SF.shutdown();
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
    public static SF sf() {
        return SF.sf();
    }
}
