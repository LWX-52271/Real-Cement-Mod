package lwx.cementmod.jei.cementpacker;

import lwx.cementmod.CementMod;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CementPackerRecipeCategory implements IRecipeCategory<CementPackerRecipe> {
    public static final String UID = CementMod.MODID + ".cement_packer";

    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final String title;

    public CementPackerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation texture = new ResourceLocation(CementMod.MODID, "textures/gui/cement_packer.png");
        this.background = guiHelper.createDrawable(texture, 0, 0, 175, 82);
        this.title = I18n.format("jei.category.cement_packer");

        // 修复进度条
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
    public void setRecipe(IRecipeLayout recipeLayout, CementPackerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();

        // 修正后的槽位布局
        stacks.init(0, true, 55, 25);   // 水泥粉 (上方)
        stacks.init(1, true, 55, 44);   // 皮革 (下方)
        stacks.init(2, false, 115, 34); // 输出 (右方)

        // 修复：使用正确的槽位设置方法
        stacks.set(0, ingredients.getInputs(ItemStack.class).get(0)); // 水泥粉
        stacks.set(1, ingredients.getInputs(ItemStack.class).get(1)); // 皮革
        stacks.set(2, ingredients.getOutputs(ItemStack.class).get(0)); // 输出
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        // 修正进度条位置 (居中)
        progress.draw(minecraft, 79, 35);
        // 添加合成时间信息
        minecraft.fontRenderer.drawString("合成时间: 5秒", 79, 55, 0x404040);
    }
}