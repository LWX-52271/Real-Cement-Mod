package com.luowenxuan.cementmod.jei.rotarykiln;

import com.luowenxuan.cementmod.CementMod;
import com.luowenxuan.cementmod.item.ItemRegistryHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RotaryKilnRecipeMaker {
    public static List<RotaryKilnRecipe> getRecipes() {
        List<RotaryKilnRecipe> recipes = new ArrayList<>();

        // 添加主要烧制配方 - 生料混合物 -> 水泥粉
        recipes.add(new RotaryKilnRecipe(
                new ItemStack(ItemRegistryHandler.RAW_MIXTURE),
                new ItemStack(ItemRegistryHandler.CEMENT_POWDER),
                200 // 10秒烧制时间
        ));

        // 添加所有原版熔炉配方
        Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.instance().getSmeltingList();
        for (Map.Entry<ItemStack, ItemStack> entry : smeltingList.entrySet()) {
            ItemStack input = entry.getKey().copy();
            ItemStack output = entry.getValue().copy();

            // 跳过生料混合物配方（已单独添加）
            if (input.getItem() == ItemRegistryHandler.RAW_MIXTURE) continue;

            // 添加配方（使用原版熔炉时间）
            recipes.add(new RotaryKilnRecipe(input, output, 200));
        }

        // 添加燃料配方
        addFuelRecipes(recipes);

        return recipes;
    }

    private static void addFuelRecipes(List<RotaryKilnRecipe> recipes) {
        // 添加所有原版燃料
        for (Item item : ForgeRegistries.ITEMS) {
            ItemStack stack = new ItemStack(item);
            int burnTime = TileEntityFurnace.getItemBurnTime(stack);
            if (burnTime > 0) {
                recipes.add(new RotaryKilnRecipe(stack, burnTime));
            }
        }}

    private static void addFuelRecipe(List<RotaryKilnRecipe> recipes, ItemStack fuelStack, int burnTime) {
        int actualBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
        if (actualBurnTime > 0) {
            recipes.add(new RotaryKilnRecipe(fuelStack, actualBurnTime));
        }
    }
}