package cn.ZeroEngine.Engine.api.v3;

import org.bukkit.plugin.java.JavaPlugin;

import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF;

/**
 旧版兼容桥接类。
 外部插件历史上通过 SF.init(plugin) / SF.sf() 获取 API 实现。
 内部实现已迁移到 cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.ZeroSF，
 本类只作为委托入口，保持调用 API 完全一致。
 */
public final class SF {

    private SF() {}

    public static void init(JavaPlugin plugin) {
        ZeroSF.init(plugin);
    }

    public static void shutdown() {
        ZeroSF.shutdown();
    }

    public static ZeroSF sf() {
        return ZeroSF.sf();
    }
}
