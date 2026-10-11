package cn.ZeroEngine.Engine.api.v3.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

/**
 * Essentials 经济后端（v3）。
 * 使用反射调用 Essentials API，避免硬依赖导致插件无法加载。
 */
public final class EssentialsBackend implements EconomyBackend {

    private final boolean loaded;
    private final Class<?> economyClass;

    public EssentialsBackend() {
        boolean l = false;
        Class<?> cls = null;
        try {
            if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
                cls = Class.forName("com.earth2me.essentials.api.Economy");
                l = true;
            }
        } catch (ClassNotFoundException ignored) {
        }
        this.loaded = l;
        this.economyClass = cls;
    }

    @Override
    public boolean ready() {
        return loaded;
    }

    @Override
    public boolean hasAccount(OfflinePlayer p) {
        return loaded && p.getName() != null;
    }

    @Override
    public double balance(OfflinePlayer p) {
        if (!loaded || p.getName() == null || economyClass == null) return 0;
        try {
            Object result = economyClass.getMethod("getMoneyExact", String.class).invoke(null, p.getName());
            if (result instanceof BigDecimal) return ((BigDecimal) result).doubleValue();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    @Override
    public boolean give(OfflinePlayer p, double amount) {
        if (!loaded || p.getName() == null || economyClass == null) return false;
        try {
            BigDecimal newBal = new BigDecimal(Double.toString(balance(p) + amount));
            economyClass.getMethod("setMoney", String.class, BigDecimal.class).invoke(null, p.getName(), newBal);
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public boolean take(OfflinePlayer p, double amount) {
        if (!loaded || p.getName() == null || economyClass == null) return false;
        try {
            BigDecimal newBal = new BigDecimal(Double.toString(balance(p) - amount));
            economyClass.getMethod("setMoney", String.class, BigDecimal.class).invoke(null, p.getName(), newBal);
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public boolean set(OfflinePlayer p, double amount) {
        if (!loaded || p.getName() == null || economyClass == null) return false;
        try {
            economyClass.getMethod("setMoney", String.class, BigDecimal.class)
                    .invoke(null, p.getName(), new BigDecimal(Double.toString(amount)));
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public String format(double amount) {
        return String.format("%.2f", amount);
    }
}
