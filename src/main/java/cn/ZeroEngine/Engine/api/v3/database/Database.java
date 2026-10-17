package cn.ZeroEngine.Engine.api.v3.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 轻量表级 CRUD 接口 — 让调用方不用拼 SQL。
 * <p>
 * 只覆盖最常见 CRUD（单表、等值条件）。需要 JOIN / 子查询 / 复杂表达式时，继续用底层
 * {@link #executeQuery(String, Function, Object...)} / {@link #executeUpdate(String, Object...)}。
 * <p>
 * 新增方法都是 {@code default}，委托给底层 {@code executeQuery/executeUpdate}，
 * MySQLDatabase / SQLiteDatabase 实现不用改。
 */
public interface Database {

    boolean connect();
    void disconnect();
    boolean isConnected();
    Connection connection();

    /** 底层原生执行（保留给高级场景：DDL、JOIN、复杂查询）。 */
    int executeUpdate(String sql, Object... params);

    /** 底层原生查询（保留给高级场景）。 */
    <T> T executeQuery(String sql, Function<ResultSet, T> mapper, Object... params);

    default PreparedStatement stmt(Connection c, String sql, Object... params) throws Exception {
        PreparedStatement ps = c.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }

    // ========= 表级轻量 CRUD =========

    /** 返回整张表所有行。 */
    default List<DataRow> all(String table) {
        return executeQuery("SELECT * FROM " + table, Database::rowList, new Object[0]);
    }

    /** 条件查询：data.get("homes", Map.of("uuid", uuid))。 */
    default List<DataRow> get(String table, Map<String, Object> where) {
        SqlAndArgs sa = buildWhere(where);
        return executeQuery("SELECT * FROM " + table + " WHERE " + sa.sql, Database::rowList, sa.args);
    }

    /** 取单条（首行）。 */
    default DataRow first(String table, Map<String, Object> where) {
        List<DataRow> list = get(table, where);
        return list.isEmpty() ? null : list.get(0);
    }

    /** 插入一行：data.add("homes", Map.of("uuid", uuid, "name", name))。返回受影响行数。 */
    default int add(String table, Map<String, Object> columns) {
        if (columns.isEmpty()) return 0;
        StringBuilder cols = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        Object[] args = new Object[columns.size()];
        int i = 0;
        for (Map.Entry<String, Object> e : columns.entrySet()) {
            if (i > 0) { cols.append(','); placeholders.append(','); }
            cols.append(e.getKey());
            placeholders.append('?');
            args[i++] = e.getValue();
        }
        return executeUpdate(
                "INSERT INTO " + table + " (" + cols + ") VALUES (" + placeholders + ")",
                args
        );
    }

    /** 全列覆盖式 set：先按 where 删除，再插入 columns。类似 REPLACE。 */
    default int set(String table, Map<String, Object> columns, Map<String, Object> where) {
        SqlAndArgs w = buildWhere(where);
        executeUpdate("DELETE FROM " + table + " WHERE " + w.sql, w.args);
        return add(table, columns);
    }

    /** 增量更新（只改指定列）。 */
    default int update(String table, Map<String, Object> set, Map<String, Object> where) {
        if (set.isEmpty()) return 0;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String col : set.keySet()) {
            if (i++ > 0) sb.append(',');
            sb.append(col).append("=?");
        }
        SqlAndArgs w = buildWhere(where);
        Object[] args = new Object[set.size() + w.args.length];
        i = 0;
        for (Object v : set.values()) args[i++] = v;
        System.arraycopy(w.args, 0, args, i, w.args.length);
        return executeUpdate("UPDATE " + table + " SET " + sb + " WHERE " + w.sql, args);
    }

    /** 删除。返回受影响行数。 */
    default int delete(String table, Map<String, Object> where) {
        SqlAndArgs w = buildWhere(where);
        return executeUpdate("DELETE FROM " + table + " WHERE " + w.sql, w.args);
    }

    /** 计行。 */
    default long count(String table, Map<String, Object> where) {
        SqlAndArgs w = buildWhere(where);
        Long r = executeQuery(
                "SELECT COUNT(*) FROM " + table + " WHERE " + w.sql,
                rs -> {
                    try { rs.next(); return rs.getLong(1); }
                    catch (java.sql.SQLException e) { throw new RuntimeException(e); }
                },
                w.args
        );
        return r == null ? 0L : r;
    }

    // ===== 内部辅助 — static 方法不依赖接口实现状态，纯工具 =====

    static SqlAndArgs buildWhere(Map<String, Object> where) {
        if (where == null || where.isEmpty()) {
            return new SqlAndArgs("1=1", new Object[0]);
        }
        StringBuilder sb = new StringBuilder();
        Object[] args = new Object[where.size()];
        int i = 0;
        for (Map.Entry<String, Object> e : where.entrySet()) {
            if (i > 0) sb.append(" AND ");
            sb.append(e.getKey()).append("=?");
            args[i++] = e.getValue();
        }
        return new SqlAndArgs(sb.toString(), args);
    }

    static List<DataRow> rowList(ResultSet rs) {
        try {
            List<DataRow> out = new ArrayList<>();
            int n = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                DataRow row = new DataRow();
                for (int i = 1; i <= n; i++) {
                    row.set(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                out.add(row);
            }
            return out;
        } catch (Exception e) {
            throw new RuntimeException("rowList failed", e);
        }
    }

    // ===== 类型 =====

    final class SqlAndArgs {
        final String sql;
        final Object[] args;
        SqlAndArgs(String sql, Object[] args) { this.sql = sql; this.args = args; }
    }

    /**
     * 一张记录：列名 → 值的有序容器。
     * 提供 {@code getString / getInt / getLong / getDouble / getFloat / getBoolean / get} 强类型访问器。
     */
    final class DataRow {
        private final Map<String, Object> cols = new LinkedHashMap<>();

        void set(String name, Object value) { cols.put(name, value); }

        public Object get(String name) { return cols.get(name); }

        public String getString(String name) {
            Object v = cols.get(name); return v == null ? null : v.toString();
        }
        public int getInt(String name) {
            Object v = cols.get(name);
            return v == null ? 0 : (v instanceof Number ? ((Number) v).intValue() : Integer.parseInt(v.toString()));
        }
        public long getLong(String name) {
            Object v = cols.get(name);
            return v == null ? 0L : (v instanceof Number ? ((Number) v).longValue() : Long.parseLong(v.toString()));
        }
        public double getDouble(String name) {
            Object v = cols.get(name);
            return v == null ? 0.0 : (v instanceof Number ? ((Number) v).doubleValue() : Double.parseDouble(v.toString()));
        }
        public float getFloat(String name) {
            Object v = cols.get(name);
            return v == null ? 0.0f : (v instanceof Number ? ((Number) v).floatValue() : Float.parseFloat(v.toString()));
        }
        public boolean getBoolean(String name) {
            Object v = cols.get(name); return v != null && (v instanceof Boolean ? (Boolean) v : Boolean.parseBoolean(v.toString()));
        }

        public boolean has(String name) { return cols.containsKey(name); }
        public Map<String, Object> asMap() { return cols; }
        public int size() { return cols.size(); }
    }
}
