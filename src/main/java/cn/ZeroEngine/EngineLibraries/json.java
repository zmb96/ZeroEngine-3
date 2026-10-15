package cn.ZeroEngine.EngineLibraries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/*
    这是作者自己写的json解析工具，随时可能变化，请用Gson
 */
public class json {

    private static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson RAW = new Gson();

    private final JavaPlugin plugin;
    private final File file;
    private JsonObject root;

    public json(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);
    }

    public json(JavaPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    public json load() {
        return load(true);
    }

    public json load(boolean saveFromJarIfMissing) {
        if (saveFromJarIfMissing && !file.exists()) {
            plugin.saveResource(file.getName(), false);
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            JsonElement el = JsonParser.parseReader(reader);
            root = el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
        } catch (IOException e) {
            plugin.getLogger().severe("读取 " + file.getName() + " 失败: " + e.getMessage());
            root = new JsonObject();
        }
        return this;
    }

    public json loadFromStream(InputStream in) {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonElement el = JsonParser.parseReader(reader);
            root = el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
        } catch (IOException e) {
            plugin.getLogger().severe("从流读取 JSON 失败: " + e.getMessage());
            root = new JsonObject();
        }
        return this;
    }

    public JsonObject getObject() {
        if (root == null) throw new IllegalStateException("JSON 未加载，先调用 load()");
        return root;
    }

    public void setObject(JsonObject obj) {
        this.root = obj == null ? new JsonObject() : obj;
    }

    public String getString(String key) {
        JsonElement e = getObject().get(key);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    public String getString(String key, String def) {
        String s = getString(key);
        return s == null ? def : s;
    }

    public int getInt(String key, int def) {
        JsonElement e = getObject().get(key);
        return e == null || e.isJsonNull() ? def : e.getAsInt();
    }

    public long getLong(String key, long def) {
        JsonElement e = getObject().get(key);
        return e == null || e.isJsonNull() ? def : e.getAsLong();
    }

    public double getDouble(String key, double def) {
        JsonElement e = getObject().get(key);
        return e == null || e.isJsonNull() ? def : e.getAsDouble();
    }

    public boolean getBoolean(String key, boolean def) {
        JsonElement e = getObject().get(key);
        return e == null || e.isJsonNull() ? def : e.getAsBoolean();
    }

    public JsonObject getJsonObject(String key) {
        JsonElement e = getObject().get(key);
        return e != null && e.isJsonObject() ? e.getAsJsonObject() : null;
    }

    public JsonArray getJsonArray(String key) {
        JsonElement e = getObject().get(key);
        return e != null && e.isJsonArray() ? e.getAsJsonArray() : null;
    }

    public JsonElement get(String path) {
        String[] parts = path.split("\\.");
        JsonElement cur = getObject();
        for (String p : parts) {
            if (cur == null || !cur.isJsonObject()) return null;
            cur = cur.getAsJsonObject().get(p);
        }
        return cur;
    }

    public String getStringPath(String path) {
        JsonElement e = get(path);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    public int getIntPath(String path, int def) {
        JsonElement e = get(path);
        return e == null || e.isJsonNull() ? def : e.getAsInt();
    }

    public boolean getBooleanPath(String path, boolean def) {
        JsonElement e = get(path);
        return e == null || e.isJsonNull() ? def : e.getAsBoolean();
    }

    public json set(String key, Object value) {
        getObject().add(key, RAW.toJsonTree(value));
        return this;
    }

    public json setPath(String path, Object value) {
        String[] parts = path.split("\\.");
        JsonObject cur = getObject();
        for (int i = 0; i < parts.length - 1; i++) {
            JsonElement next = cur.get(parts[i]);
            if (next == null || !next.isJsonObject()) {
                JsonObject created = new JsonObject();
                cur.add(parts[i], created);
                cur = created;
            } else {
                cur = next.getAsJsonObject();
            }
        }
        cur.add(parts[parts.length - 1], RAW.toJsonTree(value));
        return this;
    }

    public boolean has(String key) {
        return getObject().has(key);
    }

    public json remove(String key) {
        getObject().remove(key);
        return this;
    }

    public boolean save() {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            try (Writer w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                PRETTY.toJson(getObject(), w);
            }
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("保存 " + file.getName() + " 失败: " + e.getMessage());
            return false;
        }
    }

    public <T> T as(Class<T> clazz) {
        return RAW.fromJson(getObject(), clazz);
    }

    public json from(Object bean) {
        this.root = RAW.toJsonTree(bean).getAsJsonObject();
        return this;
    }

    public File getFile() {
        return file;
    }
}