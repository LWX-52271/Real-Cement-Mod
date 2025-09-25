package lwx.cementmod.jei.cementmixer;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class CementMixerRecipe implements IRecipeWrapper {
    private final ItemStack limePowder;
    private final ItemStack sand;
    private final ItemStack waterBucket;
    private final ItemStack output;

    public CementMixerRecipe(ItemStack limePowder, ItemStack sand, ItemStack waterBucket, ItemStack output) {
        this.limePowder = limePowder;
        this.sand = sand;
        this.waterBucket = waterBucket;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // 修复：使用正确的输入列表设置方法
        ingredients.setInputs(ItemStack.class, Arrays.asList(
                limePowder,
                sand,
                waterBucket
        ));
        ingredients.setOutput(ItemStack.class, output);
    }
}