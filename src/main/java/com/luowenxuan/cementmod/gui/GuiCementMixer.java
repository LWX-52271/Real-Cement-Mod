package com.luowenxuan.cementmod.gui;

import com.luowenxuan.cementmod.CementMod;
import com.luowenxuan.cementmod.container.ContainerCementMixer;
import com.luowenxuan.cementmod.tiles.TileCementMixer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiCementMixer extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CementMod.MODID, "textures/gui/cement_mixer.png");
    private final TileCementMixer tileEntity;

    public GuiCementMixer(TileCementMixer te, ContainerCementMixer container) {
        super(container);
        this.tileEntity = te;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // 从容器获取进度值，而不是直接从TileEntity获取
        ContainerCementMixer container = (ContainerCementMixer) this.inventorySlots;
        int mixProgress = container.getMixProgressScaled(24);

        // 绘制混合进度条（确保进度值在合理范围内）
        if (mixProgress > 0) {
            drawTexturedModalRect(guiLeft + 79, guiTop + 35, 176, 0, mixProgress, 17);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("tile.cement_mixer.name");
        fontRenderer.drawString(title, (xSize - fontRenderer.getStringWidth(title)) / 2, 6, 0x404040);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        // 在鼠标悬停在进度条上时显示工具提示
        if (isPointInRegion(79, 35, 24, 17, mouseX, mouseY)) {
            ContainerCementMixer container = (ContainerCementMixer) this.inventorySlots;
            int progress = container.getMixProgressScaled(100);
            drawHoveringText(I18n.format("gui.cement_mixer.progress", progress) + "%", mouseX, mouseY);
        }
    }
}