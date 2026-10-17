package cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.recipe;

import org.bukkit.Material;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v3.feature.item.ZeroMagicScepterItem;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import cn.ZeroEngine.Engine.api.v3.feature.recipe.SRecipe;

public class ZeroMagicScepterRecipe extends SRecipe {

    @Override public String id() { return "magic_scepter"; }

    @Override public RecipeMode mode() { return RecipeMode.SHAPED; }

    @Override
    public List<String> shape() {
        return Arrays.asList(
                " E ",
                "GBG",
                " D "
        );
    }

    @Override
    public Map<Character, Object> ingredients() {
        
        Map<Character, Object> map = new LinkedHashMap<>();
        map.put('E', Material.ENDER_EYE);
        map.put('G', Material.GOLD_INGOT);
        map.put('B', Material.BLAZE_ROD);
        map.put('D', Material.DIAMOND);
        return map;
    }

    @Override
    public Object result() { return new ZeroMagicScepterItem(); }

    @Override
    public int resultAmount() { return 1; }
}
