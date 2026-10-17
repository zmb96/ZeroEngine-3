package cn.ZeroEngine.Engine.api.v3.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LocationStorage — homes / warps / last_locations 的表级 CRUD。
 * 所有 SQL 拼接集中在 {@link Database} 接口的默认方法里，本类只写表名 + 列值 + 条件。
 */
public final class LocationStorage {

    private LocationStorage() {
    }

    // ---------- homes ----------

    public static boolean setHome(UUID uuid, String name, LocationData loc) {
        Map<String, Object> cols = homeCols(uuid, name, loc);
        Map<String, Object> where = Map.of("uuid", uuid.toString(), "name", name);
        return DatabaseManager.db().set("homes", cols, where) > 0;
    }

    public static boolean delHome(UUID uuid, String name) {
        return DatabaseManager.db().delete("homes",
                Map.of("uuid", uuid.toString(), "name", name)) > 0;
    }

    public static LocationData getHome(UUID uuid, String name) {
        Database.DataRow row = DatabaseManager.db().first("homes",
                Map.of("uuid", uuid.toString(), "name", name));
        return row == null ? null : readLoc(row);
    }

    public static Map<String, LocationData> getHomes(UUID uuid) {
        List<Database.DataRow> rows = DatabaseManager.db().get("homes",
                Map.of("uuid", uuid.toString()));
        Map<String, LocationData> out = new HashMap<>();
        for (Database.DataRow r : rows) out.put(r.getString("name"), readLoc(r));
        return out;
    }

    // ---------- warps ----------

    public static boolean setWarp(String name, LocationData loc, UUID createdBy) {
        Map<String, Object> cols = new LinkedHashMap<>();
        cols.put("name", name);
        cols.put("world", loc.world);
        cols.put("x", loc.x);
        cols.put("y", loc.y);
        cols.put("z", loc.z);
        cols.put("yaw", loc.yaw);
        cols.put("pitch", loc.pitch);
        cols.put("created_by", createdBy == null ? null : createdBy.toString());
        cols.put("created_at", System.currentTimeMillis());
        return DatabaseManager.db().set("warps", cols, Map.of("name", name)) > 0;
    }

    public static boolean delWarp(String name) {
        return DatabaseManager.db().delete("warps", Map.of("name", name)) > 0;
    }

    public static LocationData getWarp(String name) {
        Database.DataRow row = DatabaseManager.db().first("warps", Map.of("name", name));
        return row == null ? null : readLoc(row);
    }

    public static Map<String, LocationData> getWarps() {
        List<Database.DataRow> rows = DatabaseManager.db().all("warps");
        Map<String, LocationData> out = new HashMap<>();
        for (Database.DataRow r : rows) out.put(r.getString("name"), readLoc(r));
        return out;
    }

    // ---------- last_locations ----------

    public static boolean setLastLocation(UUID uuid, LocationData loc) {
        Map<String, Object> cols = homeCols(uuid, null, loc); // name 列在这张表不存在
        cols.remove("name");
        return DatabaseManager.db().set("last_locations", cols, Map.of("uuid", uuid.toString())) > 0;
    }

    public static LocationData getLastLocation(UUID uuid) {
        Database.DataRow row = DatabaseManager.db().first("last_locations", Map.of("uuid", uuid.toString()));
        return row == null ? null : readLoc(row);
    }

    // ---------- 辅助 ----------

    private static Map<String, Object> homeCols(UUID uuid, String name, LocationData loc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("uuid", uuid.toString());
        if (name != null) m.put("name", name);
        m.put("world", loc.world);
        m.put("x", loc.x);
        m.put("y", loc.y);
        m.put("z", loc.z);
        m.put("yaw", loc.yaw);
        m.put("pitch", loc.pitch);
        return m;
    }

    private static LocationData readLoc(Database.DataRow row) {
        return new LocationData(
                row.getString("world"),
                row.getDouble("x"), row.getDouble("y"), row.getDouble("z"),
                row.getFloat("yaw"), row.getFloat("pitch")
        );
    }

    public static List<String> homeNames(UUID uuid) {
        return new ArrayList<>(getHomes(uuid).keySet());
    }

    public static List<String> warpNames() {
        return new ArrayList<>(getWarps().keySet());
    }
}
