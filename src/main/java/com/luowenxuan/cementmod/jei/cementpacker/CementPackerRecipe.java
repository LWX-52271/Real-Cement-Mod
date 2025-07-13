package com.luowenxuan.cementmod.jei.cementpacker;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public class CementPackerRecipe implements IRecipeWrapper {
    private final ItemStack cementPowder;
    private final ItemStack leather;
    private final ItemStack output;

    public CementPackerRecipe(ItemStack cementPowder, ItemStack leather, ItemStack output) {
        this.cementPowder = cementPowder;
        this.leather = leather;
        this.output = output;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // 修复：使用正确的输入列表设置方法
        ingredients.setInputs(ItemStack.class, Arrays.asList(
                cementPowder,
                leather
        ));
        ingredients.setOutput(ItemStack.class, output);
    }
}
