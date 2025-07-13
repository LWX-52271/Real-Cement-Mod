package com.luowenxuan.cementmod.jei.crusher;

import com.luowenxuan.cementmod.block.BlockRegistryHandler;
import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import com.luowenxuan.cementmod.tiles.TileCrusher;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class CrusherRecipeMaker {
    public static List<CrusherRecipe> getRecipes() {
        List<CrusherRecipe> recipes = new ArrayList<>();

        // 添加所有配方
        for (TileCrusher.CrusherRecipe recipe : TileCrusher.CRUSHER_RECIPES) {
            ItemStack input;

            if (recipe.input instanceof ItemStack) {
                input = ((ItemStack) recipe.input).copy();
            } else if (recipe.input instanceof Block) {
                input = new ItemStack((Block) recipe.input);
            } else if (recipe.input instanceof String) {
                // 获取矿辞的第一个物品作为代表
                List<ItemStack> ores = OreDictionary.getOres((String) recipe.input);
                input = ores.isEmpty() ? ItemStack.EMPTY : ores.get(0).copy();
            } else {
                continue;
            }

            if (!input.isEmpty()) {
                ItemStack output = recipe.getOutput();
                output.setCount(recipe.getOutputCount());
                recipes.add(new CrusherRecipe(input, output));
            }
        }

        return recipes;
    }
}