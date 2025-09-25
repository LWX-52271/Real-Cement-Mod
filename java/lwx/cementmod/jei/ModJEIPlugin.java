package lwx.cementmod.jei;

import lwx.cementmod.gui.GuiRotaryKiln;
import lwx.cementmod.registry.BlockRegistryHandler;
import lwx.cementmod.jei.cementpacker.CementPackerRecipe;
import lwx.cementmod.jei.cementpacker.CementPackerRecipeCategory;
import lwx.cementmod.jei.cementpacker.CementPackerRecipeMaker;
import lwx.cementmod.jei.cementmixer.CementMixerRecipe;
import lwx.cementmod.jei.cementmixer.CementMixerRecipeCategory;
import lwx.cementmod.jei.cementmixer.CementMixerRecipeMaker;
import lwx.cementmod.jei.crusher.CrusherRecipe;
import lwx.cementmod.jei.crusher.CrusherRecipeCategory;
import lwx.cementmod.jei.crusher.CrusherRecipeMaker;
import lwx.cementmod.jei.rotarykiln.RotaryKilnRecipe;
import lwx.cementmod.jei.rotarykiln.RotaryKilnRecipeCategory;
import lwx.cementmod.jei.rotarykiln.RotaryKilnRecipeMaker;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import java.util.List;

@JEIPlugin
public class ModJEIPlugin implements IModPlugin {

    private IIngredientRegistry ingredientRegistry;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        // 正确方式：单独添加每个类别
        registry.addRecipeCategories(
                new CrusherRecipeCategory(guiHelper)
        );
        registry.addRecipeCategories(
                new RotaryKilnRecipeCategory(guiHelper)
        );
        registry.addRecipeCategories(
                new CementMixerRecipeCategory(guiHelper)
        );
        registry.addRecipeCategories(
                new CementPackerRecipeCategory(guiHelper)
        );
    }

    @Override
    public void register(IModRegistry registry) {
        // 获取原料注册表
        this.ingredientRegistry = registry.getIngredientRegistry();

        // 添加破碎机配方
        List<CrusherRecipe> crusherRecipes = CrusherRecipeMaker.getRecipes();
        registry.addRecipes(crusherRecipes, CrusherRecipeCategory.UID);
        registry.addRecipeCatalyst(
                new ItemStack(BlockRegistryHandler.BLOCK_CRUSHER),
                CrusherRecipeCategory.UID
        );

        // 添加回转窑配方
        List<RotaryKilnRecipe> kilnRecipes = RotaryKilnRecipeMaker.getRecipes();
        registry.addRecipes(kilnRecipes, RotaryKilnRecipeCategory.UID);
        registry.addRecipeCatalyst(
                new ItemStack(BlockRegistryHandler.BLOCK_ROTARY_KILN),
                RotaryKilnRecipeCategory.UID
        );

        // 添加水泥搅拌机配方
        List<CementMixerRecipe> mixerRecipes = CementMixerRecipeMaker.getRecipes();
        registry.addRecipes(mixerRecipes, CementMixerRecipeCategory.UID);
        registry.addRecipeCatalyst(
                new ItemStack(BlockRegistryHandler.BLOCK_CEMENT_MIXER),
                CementMixerRecipeCategory.UID
        );

        // 添加水泥打包机配方
        List<CementPackerRecipe> packerRecipes = CementPackerRecipeMaker.getRecipes();
        registry.addRecipes(packerRecipes, CementPackerRecipeCategory.UID);
        registry.addRecipeCatalyst(
                new ItemStack(BlockRegistryHandler.BLOCK_CEMENT_PACKER),
                CementPackerRecipeCategory.UID
        );

        // 添加配方点击区域
        registry.addRecipeClickArea(
                GuiRotaryKiln.class,
                80, 35, 22, 15,
                RotaryKilnRecipeCategory.UID
        );
    }
}