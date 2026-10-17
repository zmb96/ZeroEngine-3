package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import cn.ZeroEngine.Engine.api.v2.database.Database;
import cn.ZeroEngine.Engine.api.v2.database.DatabaseManager;
import cn.ZeroEngine.Engine.api.v2.economy.SFEconomy;
import cn.ZeroEngine.Engine.api.v2.event.SFEvents;
import cn.ZeroEngine.Engine.api.v2.feature.engine.BlockControl;
import cn.ZeroEngine.Engine.api.v2.feature.engine.DamageSystem;
import cn.ZeroEngine.Engine.api.v2.feature.engine.MonsterAttribute;
import cn.ZeroEngine.Engine.api.v2.feature.engine.ResourcePackManager;
import cn.ZeroEngine.Engine.api.v2.feature.engine.SpawnControl;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.enchant.ZeroEnchantAttributeListener;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.enchant.ZeroEnchantManager;
import cn.ZeroEngine.Engine.api.v2.feature.enchant.SEnchantment;
import cn.ZeroEngine.Engine.api.v2.feature.enchant.SFAttr;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.item.ZeroItemListener;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.item.ZeroItemManager;
import cn.ZeroEngine.Engine.api.v2.feature.item.SItem;
import cn.ZeroEngine.Engine.api.v2.feature.teleport.TeleportManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.tick.ZeroTickManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.chat.ZeroChatManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.world.ZeroWorldManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.permission.ZeroPermissionManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.main.ZeroReachManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.perf.ZeroPerformanceManager;
import cn.ZeroEngine.Engine.api.v2.main.SFCommandOps;
import cn.ZeroEngine.Engine.api.v2.main.SFLogger;
import cn.ZeroEngine.Engine.api.v2.main.SFPlayerOps;
import cn.ZeroEngine.Engine.api.v2.main.SFScheduler;
import cn.ZeroEngine.Engine.api.v2.main.SFServerOps;

import java.util.UUID;
import cn.ZeroEngine.Engine.api.v2.SFApi;

public final class ZeroSF implements SFApi {

    private static ZeroSF instance;

    private final JavaPlugin plugin;
    private final SFEconomy economy;
    private final SFEvents events;
    private final SFLogger logger;
    private final SFScheduler scheduler;
    private final SFPlayerOps players;
    private final SFCommandOps commands;
    private final SFServerOps serverOps;
    private final ZeroTickManager tickManager;
    private TeleportManager teleportManager;
    private ZeroEnchantManager enchantManager;
    private ZeroEnchantAttributeListener enchantAttrListener;
    private ZeroItemManager itemManager;
    private ZeroItemListener itemListener;
    private ZeroChatManager chatManager;
    private ZeroWorldManager worldManager;
    private ZeroPermissionManager permissionManager;
    private ZeroReachManager reachManager;
    private ZeroPerformanceManager perfManager;
    private MonsterAttribute monsterAttr;
    private DamageSystem damageSys;
    private BlockControl blockCtrl;
    private SpawnControl spawnCtrl;
    private ResourcePackManager resourcePackMgr;

    private ZeroSF(JavaPlugin plugin) {
        this.plugin = plugin;
        this.economy = new SFEconomy(plugin);
        this.events = new SFEvents(plugin);
        this.logger = new SFLogger(plugin);
        this.scheduler = new SFScheduler(plugin);
        this.players = new SFPlayerOps();
        this.commands = new SFCommandOps(plugin);
        this.serverOps = new SFServerOps();
        this.tickManager = new ZeroTickManager(plugin);
        this.tickManager.start();
        DatabaseManager.init(plugin);
    }

    public static void init(JavaPlugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("ZeroSF already initialized");
        }
        instance = new ZeroSF(plugin);
        plugin.getServer().getServicesManager().register(SFApi.class, instance, plugin, org.bukkit.plugin.ServicePriority.Normal);
        plugin.getServer().getServicesManager().register(ZeroSF.class, instance, plugin, org.bukkit.plugin.ServicePriority.Normal);
    }

    public static void shutdown() {
        if (instance != null) {
            if (instance.enchantAttrListener != null) instance.enchantAttrListener.shutdown();
            if (instance.enchantManager != null) instance.enchantManager.unregisterAll();
        if (instance.itemListener != null) instance.itemListener.shutdown();
        if (instance.itemManager != null) instance.itemManager.unregisterAll();
        if (instance.perfManager != null) instance.perfManager.shutdown();
            instance.events.unregisterAll();
            instance.tickManager.shutdown();
            DatabaseManager.shutdown();
            instance.plugin.getServer().getServicesManager().unregister(instance);
        }
        instance = null;
    }

    public static ZeroSF sf() {
        if (instance == null) {
            throw new IllegalStateException("ZeroSF not initialized. Call ZeroSF.init(plugin) in onEnable().");
        }
        return instance;
    }

    public void setTeleportManager(TeleportManager tp) {
        this.teleportManager = tp;
    }

    public TeleportManager teleport() {
        return teleportManager;
    }

    public ZeroEnchantManager enchant() {
        if (enchantManager == null) {
            SEnchantment.init(plugin);
            enchantManager = new ZeroEnchantManager();
            enchantAttrListener = new ZeroEnchantAttributeListener(enchantManager);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.enchant.ZeroEnchantAnvilListener(enchantManager), plugin);
            regEvent(enchantAttrListener, plugin);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.enchant.ZeroEnchantChestListener(enchantManager), plugin);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.enchant.ZeroEnchantTableListener(enchantManager), plugin);
            enchantAttrListener.startTick(this, 40L);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Enchant] System initialized");
        }
        return enchantManager;
    }

    public ZeroItemManager item() {
        if (itemManager == null) {
            SItem.init(plugin);
            itemManager = new ZeroItemManager();
            itemListener = new ZeroItemListener(itemManager);
            regEvent(itemListener, plugin);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.item.ZeroItemChestListener(itemManager), plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Item] System initialized");
        }
        return itemManager;
    }

    public ZeroChatManager chat() {
        if (chatManager == null) {
            chatManager = new ZeroChatManager(tickManager);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.chat.ZeroChatListener(chatManager), plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Chat] System initialized");
        }
        return chatManager;
    }

    @Override
    public boolean isPluginListenerChat(Player player) {
        return chatManager != null && chatManager.isPluginListening(player);
    }

    @Override
    public boolean isPluginListenerChat(UUID playerId) {
        return chatManager != null && chatManager.isPluginListening(playerId);
    }

    public ZeroWorldManager world() {
        if (worldManager == null) {
            worldManager = new ZeroWorldManager();
            ZeroSF sf = ZeroSF.sf();
            sf.info("[World] System initialized");
        }
        return worldManager;
    }

    public ZeroPermissionManager permission() {
        if (permissionManager == null) {
            permissionManager = new ZeroPermissionManager();
            permissionManager.initDefaults();
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.permission.ZeroPermissionListener(permissionManager), plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Permission] System initialized");
        }
        return permissionManager;
    }

    public ZeroReachManager reach() {
        if (reachManager == null) {
            reachManager = new ZeroReachManager();
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Reach] System initialized");
        }
        return reachManager;
    }

    public ZeroPerformanceManager perf() {
        if (perfManager == null) {
            perfManager = new ZeroPerformanceManager(plugin);
            perfManager.start();
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.perf.ZeroPerformanceListener(perfManager), plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Perf] System initialized");
        }
        return perfManager;
    }

    @Override
    public SFAttr attr() {
        SFAttr.ensureLoaded();
        return SFAttr.INSTANCE;
    }

    @Override
    public Database database() {
        return DatabaseManager.db();
    }

    @Override
    public MonsterAttribute monster() {
        if (monsterAttr == null) {
            monsterAttr = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.engine.ZeroMonsterAttributeImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] MonsterAttribute initialized");
        }
        return monsterAttr;
    }

    @Override
    public DamageSystem damage() {
        if (damageSys == null) {
            damageSys = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.engine.ZeroDamageSystemImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] DamageSystem initialized");
        }
        return damageSys;
    }

    @Override
    public BlockControl block() {
        if (blockCtrl == null) {
            blockCtrl = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.engine.ZeroBlockControlImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] BlockControl initialized");
        }
        return blockCtrl;
    }

    @Override
    public SpawnControl spawn() {
        if (spawnCtrl == null) {
            spawnCtrl = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.engine.ZeroSpawnControlImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] SpawnControl initialized");
        }
        return spawnCtrl;
    }

    @Override
    public ResourcePackManager resourcePack() {
        if (resourcePackMgr == null) {
            resourcePackMgr = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.engine.ZeroResourcePackManagerImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] ResourcePackManager initialized");
        }
        return resourcePackMgr;
    }

    @Override
    public SFLogger logger() {
        return logger;
    }

    @Override
    public SFEconomy economy() {
        return economy;
    }

    public SFEconomy eco() {
        return economy;
    }

    @Override
    public SFEvents events() {
        return events;
    }

    @Override
    public SFScheduler scheduler() {
        return scheduler;
    }

    @Override
    public SFPlayerOps players() {
        return players;
    }

    @Override
    public SFServerOps server() {
        return serverOps;
    }

    public SFCommandOps commands() {
        return commands;
    }

    @Override
    public ZeroTickManager tick() {
        return tickManager;
    }

    public JavaPlugin plugin() {
        return plugin;
    }

    public Server bukkit() {
        return plugin.getServer();
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void info(String fmt, Object... args) {
        logger.info(fmt, args);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(String fmt, Object... args) {
        logger.warn(fmt, args);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }

    @Override
    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    @Override
    public void error(String fmt, Object... args) {
        logger.error(fmt, args);
    }

    @Override
    public void broadcast(String msg) {
        serverOps.broadcast(msg);
    }

    @Override
    public void broadcast(String perm, String msg) {
        serverOps.broadcast(perm, msg);
    }

    @Override
    public void msg(CommandSender sender, String msg) {
        serverOps.msg(sender, msg);
    }

    @Override
    public Player player(String name) {
        return players.byName(name);
    }

    @Override
    public Player player(UUID id) {
        return players.byId(id);
    }

    @Override
    public boolean giveMoney(OfflinePlayer p, double amount) {
        return economy.give(p, amount);
    }

    @Override
    public boolean takeMoney(OfflinePlayer p, double amount) {
        return economy.take(p, amount);
    }

    @Override
    public boolean setMoney(OfflinePlayer p, double amount) {
        return economy.set(p, amount);
    }

    @Override
    public double balance(OfflinePlayer p) {
        return economy.balance(p);
    }

    @Override
    public boolean transferMoney(OfflinePlayer from, OfflinePlayer to, double amount) {
        return economy.transfer(from, to, amount);
    }

    @Override
    public String formatMoney(double amount) {
        return economy.format(amount);
    }

    @Override
    public boolean teleport(Player p, Location loc) {
        if (teleportManager != null) {
            return teleportManager.teleportNow(p, loc, "api");
        }
        return p.teleport(loc);
    }

    @Override
    public void run(Runnable r) {
        scheduler.run(r);
    }

    @Override
    public void runAsync(Runnable r) {
        scheduler.runAsync(r);
    }

    @Override
    public void runLater(Runnable r, long ticks) {
        scheduler.runLater(r, ticks);
    }

    @Override
    public void runTimer(Runnable r, long delay, long period) {
        scheduler.runTimer(r, delay, period);
    }

    @Override
    public void console(String cmd) {
        commands.console(cmd);
    }

    public ZeroSF regEvent(Listener listener, JavaPlugin p) {
        commands.regEvent(listener, p);
        return this;
    }

    public ZeroSF regEvent(Listener listener) {
        commands.regEvent(listener);
        return this;
    }

    public ZeroSF regCommand(String name, CommandExecutor executor) {
        commands.regCommand(name, executor);
        return this;
    }
}
