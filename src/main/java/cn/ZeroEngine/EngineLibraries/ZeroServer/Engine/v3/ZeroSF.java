package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3;

import cn.ZeroEngine.EngineLibraries.ZeroServer.json;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v1.Zeromain;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import cn.ZeroEngine.Engine.api.v3.database.Database;
import cn.ZeroEngine.Engine.api.v3.database.DatabaseManager;
import cn.ZeroEngine.Engine.api.v3.economy.SFEconomy;
import cn.ZeroEngine.Engine.api.v3.event.SFEvents;
import cn.ZeroEngine.Engine.api.v3.feature.engine.BlockControl;
import cn.ZeroEngine.Engine.api.v3.feature.engine.DamageSystem;
import cn.ZeroEngine.Engine.api.v3.feature.engine.MonsterAttribute;
import cn.ZeroEngine.Engine.api.v3.feature.engine.ResourcePackManager;
import cn.ZeroEngine.Engine.api.v3.feature.engine.SpawnControl;
import cn.ZeroEngine.Engine.api.v3.feature.gui.GUIManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.gui.ZeroGUIManagerImpl;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroEnchantAttributeListener;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroEnchantManager;
import cn.ZeroEngine.Engine.api.v3.feature.enchant.SEnchantment;
import cn.ZeroEngine.Engine.api.v3.feature.enchant.SFAttr;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.achievement.ZeroAchievementManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.achievement.ZeroAchievementListener;
import cn.ZeroEngine.Engine.api.v3.feature.achievement.SAchievement;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.achievement.ZeroSFAchievementCommand;
import cn.ZeroEngine.Engine.api.v3.feature.gameplay.bedwars.Bedwars;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.gameplay.bedwars.ZeroBedwarsImpl;
import cn.ZeroEngine.Engine.api.v3.feature.gameplay.horde.Horde;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.gameplay.horde.ZeroHordeImpl;
import cn.ZeroEngine.Engine.api.v3.feature.gameplay.pvp.PvPArena;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.gameplay.pvp.ZeroPvPArenaImpl;
import cn.ZeroEngine.Engine.api.v3.feature.gameplay.village.VillageDefense;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.gameplay.village.ZeroVillageDefenseImpl;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.item.ZeroItemListener;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.item.ZeroItemManager;
import cn.ZeroEngine.Engine.api.v3.feature.item.SItem;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.entity.ZeroEntityManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.entity.ZeroEntityListener;
import cn.ZeroEngine.Engine.api.v3.feature.entity.SEntity;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.recipe.ZeroRecipeManager;
import cn.ZeroEngine.Engine.api.v3.feature.recipe.SRecipe;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.block.ZeroBlockManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.block.ZeroBlockListener;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.block.ZeroSFBlockCommand;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.screen.ZeroScreenManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.crop.ZeroCropManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.crop.ZeroCropListener;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.crop.ZeroSFCropCommand;
import cn.ZeroEngine.Engine.api.v3.feature.teleport.TeleportManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.tick.ZeroTickManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.chat.ZeroChatManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.world.ZeroWorldManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.permission.ZeroPermissionManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.main.ZeroReachManager;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.perf.ZeroPerformanceManager;
import cn.ZeroEngine.Engine.api.v3.main.SFCommandOps;
import cn.ZeroEngine.Engine.api.v3.main.SFLogger;
import cn.ZeroEngine.Engine.api.v3.main.SFPlayerOps;
import cn.ZeroEngine.Engine.api.v3.main.SFScheduler;
import cn.ZeroEngine.Engine.api.v3.main.SFServerOps;

import java.util.UUID;
import cn.ZeroEngine.Engine.api.v3.SFApi;

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
    private final String verison;
    private TeleportManager teleportManager;
    private ZeroEnchantManager enchantManager;
    private ZeroEnchantAttributeListener enchantAttrListener;
    private ZeroItemManager itemManager;
    private ZeroItemListener itemListener;
    private ZeroEntityManager entityManager;
    private ZeroEntityListener entityListener;
    private ZeroRecipeManager recipeManager;
    private ZeroBlockManager blockManager;
    private ZeroBlockListener blockListener;
    private ZeroScreenManager screenManager;
    private ZeroCropManager cropManager;
    private ZeroCropListener cropListener;
    private cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.biome.ZeroBiomeManager biomeManager;
    private cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.biome.ZeroBiomeListener biomeListener;
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
    private GUIManager guiMgr;
    private Bedwars bedwarsMgr;
    private ZeroBedwarsImpl bedwarsImpl;
    private PvPArena pvpArena;
    private ZeroPvPArenaImpl pvpImpl;
    private Horde hordeMgr;
    private ZeroHordeImpl hordeImpl;
    private VillageDefense villageDef;
    private ZeroVillageDefenseImpl villageDefImpl;
    private ZeroAchievementManager achievementManager;

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
        this.verison = Zeromain.ZeroEngine_VERSION;
        DatabaseManager.init(plugin);
    }

    public String getVersion() {
        return this.verison;
    }

    public String getZeroEngineJson() {
        json lang = new json(this.plugin(), "lang.json").load();
        plugin.getLogger().info(lang.getStringPath("warn.ZeroSF.JsonWarn"));
        return Zeromain.ZeroEngine_GunMu;
    }

    public static void init(JavaPlugin plugin) {
        if (instance != null) {
            plugin.getLogger().info("[ZeroSF] already initialized by " + instance.plugin.getName()
                    + "; ignoring ZeroSF.init(" + plugin.getName() + ")");
            return;
        }
        instance = new ZeroSF(plugin);
        plugin.getServer().getServicesManager().register(SFApi.class, instance, plugin, org.bukkit.plugin.ServicePriority.Normal);
        plugin.getServer().getServicesManager().register(ZeroSF.class, instance, plugin, org.bukkit.plugin.ServicePriority.Normal);
        instance.regCommand("sfaddons", new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.addons.ZeroSFAddonsCommand());
        plugin.getLogger().info("[ZeroSF] /sfaddons command registered (hot-reload management)");
    }

    public static void shutdown() {
        if (instance != null) {
            if (instance.enchantAttrListener != null) instance.enchantAttrListener.shutdown();
            if (instance.enchantManager != null) instance.enchantManager.unregisterAll();
            if (instance.itemListener != null) instance.itemListener.shutdown();
            if (instance.itemManager != null) instance.itemManager.unregisterAll();
            if (instance.entityListener != null) instance.entityListener.shutdown();
            if (instance.entityManager != null) instance.entityManager.unregisterAll();
            if (instance.recipeManager != null) instance.recipeManager.unregisterAll();
            if (instance.blockListener != null) instance.blockListener.shutdown();
            if (instance.blockManager != null) instance.blockManager.shutdown();
            if (instance.screenManager != null) instance.screenManager.shutdown();
            if (instance.cropManager != null) instance.cropManager.shutdown();
            if (instance.cropListener != null) instance.cropListener.shutdown();
            if (instance.biomeListener != null) instance.biomeListener.shutdown();
            if (instance.biomeManager != null) instance.biomeManager.unregisterAll();
            if (instance.perfManager != null) instance.perfManager.shutdown();
            if (instance.bedwarsImpl != null) instance.bedwarsImpl.shutdown();
            if (instance.pvpImpl != null) instance.pvpImpl.shutdown();
            if (instance.hordeImpl != null) instance.hordeImpl.shutdown();
            if (instance.villageDefImpl != null) instance.villageDefImpl.shutdown();
            if (instance.achievementManager != null) instance.achievementManager.shutdown();
            if (instance.guiMgr != null) ((ZeroGUIManagerImpl) instance.guiMgr).closeAll();
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
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroEnchantAnvilListener(enchantManager), plugin);
            regEvent(enchantAttrListener, plugin);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroEnchantChestListener(enchantManager), plugin);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroEnchantTableListener(enchantManager), plugin);
            enchantAttrListener.startTick(this, 40L);
            enchantManager.registerIfAbsent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroLifestealEnchant());
            enchantManager.registerIfAbsent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroAncestralMightEnchant());
            
            
            
            
            
            regCommand("sfenchant", new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.enchant.ZeroSFEnchantCommand(enchantManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Enchant] System initialized (v3 /sfenchant command ready; " + enchantManager.all().size() + " enchants loaded)");
        }
        return enchantManager;
    }

    public ZeroItemManager item() {
        if (itemManager == null) {
            SItem.init(plugin);
            itemManager = new ZeroItemManager();
            itemListener = new ZeroItemListener(itemManager);
            regEvent(itemListener, plugin);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.item.ZeroItemChestListener(itemManager), plugin);
            itemManager.registerIfAbsent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.item.ZeroMagicScepterItem());
            regCommand("sfitem", new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.item.ZeroSFItemCommand(itemManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Item] System initialized (v3 /sfitem command ready; " + itemManager.all().size() + " items loaded)");
        }
        return itemManager;
    }

    public ZeroEntityManager entities() {
        if (entityManager == null) {
            SEntity.init(plugin);
            entityManager = new ZeroEntityManager();
            entityListener = new ZeroEntityListener(entityManager);
            regEvent(entityListener, plugin);
            entityListener.startTick(plugin, this);
            regCommand("sfentity", new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.entity.ZeroSFEntityCommand(entityManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Entity] Custom mob system initialized");
        }
        return entityManager;
    }

    public ZeroRecipeManager recipes() {
        if (recipeManager == null) {
            SRecipe.init(plugin);
            recipeManager = new ZeroRecipeManager();
            recipeManager.setItemManager(item());
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.recipe.ZeroAdvancedCraftTableListener(recipeManager), plugin);
            regCommand("sfrecipe", new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.recipe.ZeroSFRecipeCommand(recipeManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Recipe] Custom recipe system initialized (Advanced Craft Table ready)");
        }
        return recipeManager;
    }

    public ZeroBlockManager blocks() {
        if (blockManager == null) {
            blockManager = new ZeroBlockManager(plugin, item());
            blockListener = new ZeroBlockListener(plugin, blockManager);
            regEvent(blockListener, plugin);
            regCommand("sfblock", new ZeroSFBlockCommand(blockManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Block] Custom block system initialized (v3 /sfblock command ready; " + blockManager.all().size() + " blocks loaded)");
        }
        return blockManager;
    }

    public ZeroScreenManager screens() {
        if (screenManager == null) {
            screenManager = new ZeroScreenManager(plugin);
            regEvent(screenManager, plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Screen] Custom screen system initialized (v3 Dialog API, blocks join until accepted; " + screenManager.all().size() + " screens loaded)");
        }
        return screenManager;
    }

    public ZeroCropManager crops() {
        if (cropManager == null) {
            cropManager = new ZeroCropManager(plugin);
            cropListener = new ZeroCropListener(plugin, cropManager, item());
            regEvent(cropListener, plugin);
            regCommand("sfcrop", new ZeroSFCropCommand(cropManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Crop] Custom crop system initialized (v3 /sfcrop command ready; " + cropManager.all().size() + " crops loaded)");
        }
        return cropManager;
    }

    public cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.biome.ZeroBiomeManager biomes() {
        if (biomeManager == null) {
            biomeManager = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.biome.ZeroBiomeManager(this);
            biomeListener = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.biome.ZeroBiomeListener(plugin, this, biomeManager);
            regEvent(biomeListener, plugin);
            biomeListener.startTick(plugin, this);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Biome] Custom biome system initialized (ChunkPopulateEvent hook ready)");
        }
        return biomeManager;
    }

    public ZeroChatManager chat() {
        if (chatManager == null) {
            chatManager = new ZeroChatManager(tickManager);
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.chat.ZeroChatListener(chatManager), plugin);
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
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.permission.ZeroPermissionListener(permissionManager), plugin);
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
            regEvent(new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.perf.ZeroPerformanceListener(perfManager), plugin);
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
            monsterAttr = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.engine.ZeroMonsterAttributeImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] MonsterAttribute initialized");
        }
        return monsterAttr;
    }

    @Override
    public DamageSystem damage() {
        if (damageSys == null) {
            damageSys = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.engine.ZeroDamageSystemImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] DamageSystem initialized");
        }
        return damageSys;
    }

    @Override
    public BlockControl block() {
        if (blockCtrl == null) {
            blockCtrl = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.engine.ZeroBlockControlImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] BlockControl initialized");
        }
        return blockCtrl;
    }

    @Override
    public SpawnControl spawn() {
        if (spawnCtrl == null) {
            spawnCtrl = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.engine.ZeroSpawnControlImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] SpawnControl initialized");
        }
        return spawnCtrl;
    }

    @Override
    public ResourcePackManager resourcePack() {
        if (resourcePackMgr == null) {
            resourcePackMgr = new cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.engine.ZeroResourcePackManagerImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] ResourcePackManager initialized");
        }
        return resourcePackMgr;
    }

    @Override
    public GUIManager gui() {
        if (guiMgr == null) {
            guiMgr = new ZeroGUIManagerImpl(plugin);
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Engine] GUIManager initialized");
        }
        return guiMgr;
    }

    @Override
    public Bedwars bedwars() {
        if (bedwarsMgr == null) {
            bedwarsImpl = new ZeroBedwarsImpl(plugin);
            bedwarsMgr = bedwarsImpl;
            regEvent(bedwarsImpl, plugin);
            bedwarsImpl.start();
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Gameplay] Bedwars initialized");
        }
        return bedwarsMgr;
    }

    @Override
    public PvPArena pvp() {
        if (pvpArena == null) {
            pvpImpl = new ZeroPvPArenaImpl(plugin);
            pvpArena = pvpImpl;
            regEvent(pvpImpl, plugin);
            pvpImpl.start();
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Gameplay] PvPArena initialized");
        }
        return pvpArena;
    }

    @Override
    public Horde horde() {
        if (hordeMgr == null) {
            hordeImpl = new ZeroHordeImpl(plugin);
            hordeMgr = hordeImpl;
            regEvent(hordeImpl, plugin);
            hordeImpl.start();
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Gameplay] Horde initialized");
        }
        return hordeMgr;
    }

    @Override
    public VillageDefense villageDefense() {
        if (villageDef == null) {
            villageDefImpl = new ZeroVillageDefenseImpl(plugin);
            villageDef = villageDefImpl;
            regEvent(villageDefImpl, plugin);
            villageDefImpl.start();
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Gameplay] VillageDefense initialized");
        }
        return villageDef;
    }

    @Override
    public ZeroAchievementManager achievements() {
        if (achievementManager == null) {
            SAchievement.init(plugin);
            achievementManager = new ZeroAchievementManager();
            regEvent(new ZeroAchievementListener(achievementManager), plugin);
            achievementManager.generateDataPack();
            regCommand("sfadv", new ZeroSFAchievementCommand(achievementManager));
            ZeroSF sf = ZeroSF.sf();
            sf.info("[Achievement] Advancement system initialized (/sfadv, " + achievementManager.all().size() + " achievements)");
        }
        return achievementManager;
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
