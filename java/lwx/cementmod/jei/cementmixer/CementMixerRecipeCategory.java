package lwx.cementmod.jei.cementmixer;

import lwx.cementmod.CementMod;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CementMixerRecipeCategory implements IRecipeCategory<CementMixerRecipe> {
    public static final String UID = CementMod.MODID + ".cement_mixer";

    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final String title;

    public CementMixerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = new ResourceLocation(CementMod.MODID, "textures/gui/cement_mixer.png");
        this.background = guiHelper.createDrawable(texture, 0, 0, 174, 81);
        this.title = I18n.format("jei.category.cement_mixer");

        // 修复进度条位置和动画
        IDrawableStatic progressStatic = guiHelper.createDrawable(texture, 0, 166, 106, 17);
        this.progress = guiHelper.createAnimatedDrawable(progressStatic, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModName() {
        return CementMod.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CementMixerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        // 修复槽位索引和位置
        stacks.init(0, true, 37, 34);  // 水桶 (中间)
        stacks.init(1, true, 55, 16);  // 石灰粉 (左上方)
        stacks.init(2, true, 55, 52);  // 沙子 (左下方)
        stacks.init(3, false, 115, 34); // 输出 (右方)

        // 修复：使用正确的槽位设置方法
        stacks.set(0, ingredients.getInputs(ItemStack.class).get(2)); // 水桶
        stacks.set(1, ingredients.getInputs(ItemStack.class).get(0)); // 石灰粉
        stacks.set(2, ingredients.getInputs(ItemStack.class).get(1)); // 沙子
        stacks.set(3, ingredients.getOutputs(ItemStack.class).get(0)); // 输出
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        // 修正进度条位置 (居中)
        progress.draw(minecraft, 79, 35);
    }
}