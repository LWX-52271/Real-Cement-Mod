package com.luowenxuan.cementmod.gui;

import com.luowenxuan.cementmod.CementMod;
import com.luowenxuan.cementmod.container.ContainerCementPacker;
import com.luowenxuan.cementmod.tiles.TileCementPacker;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiCementPacker extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CementMod.MODID, "textures/gui/cement_packer.png");
    private final TileCementPacker tileEntity;

    public GuiCementPacker(TileCementPacker te, ContainerCementPacker container) {
        super(container);
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("tile.cement_packer.name");
        this.fontRenderer.drawString(title, this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2, 6, 0x404040);
        // 添加材料标签（可选）
        this.fontRenderer.drawString("水泥粉", 27, 30, 0x404040);
        this.fontRenderer.drawString("皮革", 27, 48, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        // 绘制进度条，直接从 tileEntity 获取进度
        int progress = tileEntity.getPackProgressScaled(24);
        this.drawTexturedModalRect(this.guiLeft + 79, this.guiTop + 35, 176, 0, progress, 17);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        if (isPointInRegion(79, 35, 24, 17, mouseX, mouseY)) {
            int progress = tileEntity.getPackProgressScaled(100);
            drawHoveringText(I18n.format("progress.cement_packer", progress) + "%", mouseX, mouseY);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.tileEntity.markDirty(); // 确保 TileEntity 数据同步
    }
}