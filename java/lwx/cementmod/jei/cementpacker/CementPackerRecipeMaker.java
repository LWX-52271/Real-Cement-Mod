package lwx.cementmod.jei.cementpacker;

import lwx.cementmod.registry.ItemRegistryHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class CementPackerRecipeMaker {
    public static List<CementPackerRecipe> getRecipes() {
        List<CementPackerRecipe> recipes = new ArrayList<>();

        // 修复：移除无效的条件判断
        ItemStack cementPowder = new ItemStack(ItemRegistryHandler.CEMENT_POWDER, 9);
        ItemStack leather = new ItemStack(Items.LEATHER, 1);
        ItemStack output = new ItemStack(ItemRegistryHandler.CEMENT_BAG, 1);

        recipes.add(new CementPackerRecipe(cementPowder, leather, output));
        return recipes;
    }
}