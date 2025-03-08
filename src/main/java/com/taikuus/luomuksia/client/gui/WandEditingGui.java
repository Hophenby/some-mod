package com.taikuus.luomuksia.client.gui;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.common.menu.WandEditingMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

//WIP
//Texture is currently using the generic_54.png texture, which is a 6x9 chest texture.
public class WandEditingGui extends AbstractContainerScreen<WandEditingMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = RegistryNames.getRL("textures/gui/container/wand_editing_gui.png");
    private static final int TEXTURE_WIDTH = 176;
    private static final int TEXTURE_HEIGHT = 222;
    private static final int DEACTIVATED_SLOT_TEXTURE_U = 0;
    private static final int WAND_SLOT_TEXTURE_U = 20;
    private static final int MISC_ICON_TEXTURE_V1 = 224;

    public WandEditingGui(WandEditingMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.inventoryLabelY += 19 + 9;
        this.titleLabelY -= 36 - 9;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - TEXTURE_WIDTH) / 2;
        int j = (this.height - TEXTURE_HEIGHT) / 2;
        pGuiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        if (!menu.isMainSlotFilled())
            pGuiGraphics.blit(CONTAINER_BACKGROUND, i + 26, j + 29, WAND_SLOT_TEXTURE_U, MISC_ICON_TEXTURE_V1, 18, 18);

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
        this.renderDeactivatedSlots(pGuiGraphics, pMouseX, pMouseY);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("container.luomuksia.wand_editing_table"), this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
    private void renderDeactivatedSlots(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        menu.getDeactivatedSlots().forEach(
                slotID -> {
                    int x = 8 + slotID % 9 * 18 - 1;
                    int y = 44 + slotID / 9 * 18 - 1;
                    guiGraphics.blit(CONTAINER_BACKGROUND, leftPos + x, topPos + y, DEACTIVATED_SLOT_TEXTURE_U, MISC_ICON_TEXTURE_V1, 18, 18);
                }
        );
    }
}
