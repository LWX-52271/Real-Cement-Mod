package com.luowenxuan.cementmod.gui;

import com.luowenxuan.cementmod.CementMod;
import com.luowenxuan.cementmod.tiles.TileItemExtractor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiItemExtractor extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CementMod.MODID, "textures/gui/item_extractor.png");
    private final TileItemExtractor tileEntity;

    public GuiItemExtractor(TileItemExtractor te, ContainerItemExtractor container) {
        super(container);
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("tile.item_extractor.name");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 0x404040);

        // 添加状态提示
        String status = tileEntity.getStatus();
        this.fontRenderer.drawString(status, 8, 72, 0x555555);

        // 添加提取计数
        String count = I18n.format("msg.extractor.count", tileEntity.getExtractedCount());
        this.fontRenderer.drawString(count, 8, 84, 0x555555);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        // 在机器上方显示工具提示
        if (isPointInRegion(62, 17, 54, 54, mouseX, mouseY)) {
            drawHoveringText(I18n.format("tooltip.extractor.storage"), mouseX, mouseY);
        }
    }
}