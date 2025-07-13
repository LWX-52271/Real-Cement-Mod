package com.luowenxuan.cementmod.jei.cementmixer;

import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class CementMixerRecipeMaker {
    public static List<CementMixerRecipe> getRecipes() {
        List<CementMixerRecipe> recipes = new ArrayList<>();
        ItemStack limePowder = new ItemStack(ItemRegistryHandler.LIME_POWDER, 1);
        ItemStack sand = new ItemStack(Blocks.SAND, 2);
        ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET, 1);
        ItemStack output = new ItemStack(ItemRegistryHandler.WET_CONCRETE_BUCKET, 1);
        recipes.add(new CementMixerRecipe(limePowder, sand, waterBucket, output));
        return recipes;
    }
}
