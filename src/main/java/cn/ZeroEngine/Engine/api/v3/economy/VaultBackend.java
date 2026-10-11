package cn.ZeroEngine.Engine.api.v3.economy;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Vault 经济后端（v3）。
 * 使用反射加载 Vault API，避免硬依赖。
 */
public final class VaultBackend implements EconomyBackend {

    private Object vault; // net.milkbowl.vault.economy.Economy
    private Class<?> vaultClass;
    private Class<?> economyResponseClass;

    public VaultBackend(JavaPlugin plugin) {
        try {
            if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
                return;
            }
            Class<?> ecoCls = Class.forName("net.milkbowl.vault.economy.Economy");
            Class<?> rspCls = Class.forName("org.bukkit.plugin.RegisteredServiceProvider");
            Object rsp = plugin.getServer().getServicesManager().getRegistration(ecoCls);
            if (rsp != null) {
                this.vault = rspCls.getMethod("getProvider").invoke(rsp);
                this.vaultClass = ecoCls;
                this.economyResponseClass = Class.forName("net.milkbowl.vault.economy.EconomyResponse");
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public boolean ready() {
        return vault != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer p) {
        if (vault == null) return false;
        try {
            return (boolean) vaultClass.getMethod("hasAccount", OfflinePlayer.class).invoke(vault, p);
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public double balance(OfflinePlayer p) {
        if (vault == null) return 0;
        try {
            Object r = vaultClass.getMethod("getBalance", OfflinePlayer.class).invoke(vault, p);
            if (r instanceof Number) return ((Number) r).doubleValue();
        } catch (Throwable ignored) {
        }
        return 0;
    }

    @Override
    public boolean give(OfflinePlayer p, double amount) {
        if (vault == null) return false;
        try {
            Object r = vaultClass.getMethod("depositPlayer", OfflinePlayer.class, double.class).invoke(vault, p, amount);
            return responseSuccess(r);
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public boolean take(OfflinePlayer p, double amount) {
        if (vault == null) return false;
        try {
            Object r = vaultClass.getMethod("withdrawPlayer", OfflinePlayer.class, double.class).invoke(vault, p, amount);
            return responseSuccess(r);
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public boolean set(OfflinePlayer p, double amount) {
        if (vault == null) return false;
        try {
            double cur = balance(p);
            if (cur > amount) {
                Object r = vaultClass.getMethod("withdrawPlayer", OfflinePlayer.class, double.class).invoke(vault, p, cur - amount);
                return responseSuccess(r);
            } else if (cur < amount) {
                Object r = vaultClass.getMethod("depositPlayer", OfflinePlayer.class, double.class).invoke(vault, p, amount - cur);
                return responseSuccess(r);
            }
            return true;
        } catch (Throwable ignored) {
        }
        return false;
    }

    private boolean responseSuccess(Object r) {
        if (r == null || economyResponseClass == null) return false;
        try {
            return (boolean) economyResponseClass.getMethod("transactionSuccess").invoke(r);
        } catch (Throwable ignored) {
        }
        return false;
    }

    @Override
    public String format(double amount) {
        if (vault == null) return String.format("%.2f", amount);
        try {
            Object r = vaultClass.getMethod("format", double.class).invoke(vault, amount);
            return r == null ? String.format("%.2f", amount) : r.toString();
        } catch (Throwable ignored) {
        }
        return String.format("%.2f", amount);
    }
}
