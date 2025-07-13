package com.luowenxuan.cementmod.gui;

import com.luowenxuan.cementmod.CementMod;
import com.luowenxuan.cementmod.container.ContainerCementBag;
import com.luowenxuan.cementmod.item.ItemCementPowder;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiCementBag extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            CementMod.MODID, "textures/gui/cement_bag.png");

    public GuiCementBag(EntityPlayer player, ItemStack bagStack) {
        super(new ContainerCementBag(player.inventory, bagStack));
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 添加半透明黑色背景
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = new TextComponentTranslation("container.cement_bag").getUnformattedText();
        fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);

        // 计算并显示总水泥粉数量
        int totalCement = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventorySlots.getSlot(i).getStack();
            if (!stack.isEmpty() && stack.getItem() instanceof ItemCementPowder) {
                totalCement += stack.getCount();
            }
        }

        String cementText = new TextComponentTranslation("tooltip.cementmod.cement_count", totalCement).getUnformattedText();
        fontRenderer.drawString(cementText, 8, 72, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}