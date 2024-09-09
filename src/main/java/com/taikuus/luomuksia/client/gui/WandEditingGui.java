package com.taikuus.luomuksia.client.gui;

import com.taikuus.luomuksia.common.menu.WandEditingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WandEditingGui extends AbstractContainerScreen<WandEditingMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

    public WandEditingGui(WandEditingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.inventoryLabelY += 19;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        int rows = 4;
        pGuiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, rows * 18 + 17);
        pGuiGraphics.blit(CONTAINER_BACKGROUND, i, j + rows * 18 + 17, 0, 126, this.imageWidth, 96);

    }
    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param pGuiGraphics the GuiGraphics object used for rendering.
     * @param pMouseX      the x-coordinate of the mouse cursor.
     * @param pMouseY      the y-coordinate of the mouse cursor.
     * @param pPartialTick the partial tick time.
     */
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
