package com.taikuus.luomuksia.client.gui;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ActionCardDeck;
import com.taikuus.luomuksia.api.wand.WandData;
import com.taikuus.luomuksia.api.wand.WrappedWandAction;
import com.taikuus.luomuksia.common.item.Wand;
import com.taikuus.luomuksia.setup.WandAttrRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class WandInfoHUD {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ResourceLocation WAND_HUD_TEXTURE = RegistryNames.getRL("textures/gui/wand_hud.png");
    private static final ResourceLocation MANA_TEXTURE = RegistryNames.getRL("textures/gui/mana.png");
    private static final ResourceLocation DELAY_TEXTURE = RegistryNames.getRL("textures/gui/delay_ticks.png");
    private static final ResourceLocation RELOAD_TEXTURE = RegistryNames.getRL("textures/gui/reload_ticks.png");
    private static final int WAND_HUD_TEXTURE_WIDTH = 60;
    private static final int WAND_HUD_TEXTURE_HEIGHT = 120;
    private static final int WAND_HUD_WIDTH = 8;
    private static final int WAND_HUD_HEIGHT = 52;
    /**
     * The last cast action deck
     */
    private static List<AbstractWandAction> lastCast = new ArrayList<>();
    public static void setLastCast(List<AbstractWandAction> lastCast) {
        WandInfoHUD.lastCast = lastCast;
    }
    public static boolean isManaHUDVisible(WandData wd){
        return wd.getMana() > 0;
    }
    public static boolean isReloadTimeHUDVisible(WandData wd){
        return wd.getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).getValue() > 0;
    }
    public static boolean isDelayTimeHUDVisible(WandData wd){
        return wd.getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).getValue() > 0;
    }

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (minecraft.options.hideGui ||// when F1 is pressed
                minecraft.player == null
        ) {
            return;
        }
        WandData wd = Wand.readData(getHeldWand());

        if (wd == null) return;
        int yAdjusted = minecraft.getWindow().getGuiScaledHeight() / 2 - WAND_HUD_HEIGHT / 2;
        if (isManaHUDVisible(wd)) {
            renderBarHUD(
                    guiGraphics,
                    deltaTracker,
                    0, 0,
                    8 * 3, 0,
                    10, yAdjusted,
                    (double) wd.getMana() / (double) wd.getMaxMana()
            );
            guiGraphics.blit(
                    MANA_TEXTURE,
                    9, yAdjusted + WAND_HUD_HEIGHT + 1,
                    0, 0,
                    8, 8,
                    8, 8
            );
        }
        if (isDelayTimeHUDVisible(wd)) {
            renderBarHUD(
                    guiGraphics,
                    deltaTracker,
                    8, 0,
                    8 * 4, 0,
                    20, yAdjusted,
                    wd.getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).getValue() /
                            wd.getMutable(WandAttrRegistry.ATTR_LAST_DELAY_TICKS).getValue()
            );
            guiGraphics.blit(
                    DELAY_TEXTURE,
                    20, yAdjusted + WAND_HUD_HEIGHT + 1,
                    0, 0,
                    8, 8,
                    8, 8
            );
        }
        if (isReloadTimeHUDVisible(wd)) {
            renderBarHUD(
                    guiGraphics,
                    deltaTracker,
                    8 * 2, 0,
                    8 * 5, 0,
                    30, yAdjusted,
                    wd.getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).getValue() /
                            wd.getMutable(WandAttrRegistry.ATTR_LAST_RELOAD_TICKS).getValue()
            );
            guiGraphics.blit(
                    RELOAD_TEXTURE,
                    30, yAdjusted + WAND_HUD_HEIGHT + 1,
                    0, 0,
                    8, 8,
                    8, 8
            );
        }

        renderDeckHUD(guiGraphics, deltaTracker, 8, yAdjusted + WAND_HUD_HEIGHT + 12, wd);
    }
    private static void renderBarHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int fullU, int fullV, int emptyU, int emptyV, int xOffset, int yOffset, double percent){
        //PoseStack ms = guiGraphics.pose();
        guiGraphics.blit(
                WAND_HUD_TEXTURE,
                xOffset,
                yOffset,
                emptyU, emptyV,
                WAND_HUD_WIDTH, WAND_HUD_HEIGHT,
                WAND_HUD_TEXTURE_WIDTH, WAND_HUD_TEXTURE_HEIGHT
        );
        guiGraphics.blit(
                WAND_HUD_TEXTURE,
                xOffset,
                yOffset + (int) (WAND_HUD_HEIGHT * (1 - percent)),
                fullU, (int) (fullV + (1 - percent) * WAND_HUD_HEIGHT),
                WAND_HUD_WIDTH, (int) (WAND_HUD_HEIGHT * percent),
                WAND_HUD_TEXTURE_WIDTH, WAND_HUD_TEXTURE_HEIGHT
        );
    }
    private static void renderDeckHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int xOffset, int yOffset, WandData wd) {
        //PoseStack ms = guiGraphics.pose();
        ActionCardDeck deck = wd.getDeck();
        ActionCardDeck discard = wd.getDiscard();

        // draw the deck
        renderActionDeck(guiGraphics, deltaTracker, xOffset, yOffset, Component.translatable("gui.luomuksia.wand.deck"), deck);
        // draw the discard
        renderActionDeck(guiGraphics,
                deltaTracker,
                xOffset,
                yOffset + 18 + Minecraft.getInstance().font.lineHeight,
                Component.translatable("gui.luomuksia.wand.discard"),
                discard);
        if (!lastCast.isEmpty()) {
            renderActionDeck(guiGraphics,
                    deltaTracker,
                    xOffset,
                    yOffset + 36 + Minecraft.getInstance().font.lineHeight * 2,
                    Component.translatable("gui.luomuksia.wand.last_cast"),
                    new ActionCardDeck(
                            IntStream.range(0, lastCast.size())
                                    .mapToObj(i -> new WrappedWandAction(lastCast.get(i), i))
                                    .toList()
                    ));
        }
    }
    private static  void renderActionDeck(GuiGraphics guiGraphics, DeltaTracker deltaTracker, int xOffset, int yOffset, Component title, ActionCardDeck deck) {
        //PoseStack ms = guiGraphics.pose();
        int deckSize = deck.size();
        // draw the deck
        String deckStr = title.getString() + " (" + deckSize + "):";
        guiGraphics.drawString(Minecraft.getInstance().font, deckStr, xOffset, yOffset, 0xFFFFFF);
        int xOffset2 = xOffset + 18;
        for (WrappedWandAction action : deck.copy().actions()) {
            // draw the action
            guiGraphics.renderFakeItem(action.action().getActionItem().getDefaultInstance(), xOffset2, yOffset + Minecraft.getInstance().font.lineHeight, 0);
            xOffset2 += 18;
        }
    }
    public static ItemStack getHeldWand(){
        if (minecraft.player == null) return ItemStack.EMPTY;
        ItemStack mainHandStack = minecraft.player.getMainHandItem();
        ItemStack offHandStack = minecraft.player.getOffhandItem();
        //priority: main hand > offhand
        return isWandValidated(mainHandStack) ? mainHandStack :
                isWandValidated(offHandStack) ? offHandStack : ItemStack.EMPTY;
    }
    private static boolean isWandValidated(ItemStack stack){
        return Wand.readData(stack) != null;
    }
}
