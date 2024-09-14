package com.taikuus.luomuksia.client.tooltip;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Map;

public record ActionTooltip(AbstractWandAction wandAction) implements TooltipComponent {
    public static class ClientActionTooltip implements ClientTooltipComponent {
        private final static ResourceLocation INFO_ICON = RegistryNames.getRL("textures/action_attr/info.png");
        private final Map<AbstractWandAction.TooltipShowableStats, String> statsWithIcon;
        public ClientActionTooltip(ActionTooltip actionTooltip) {
            this.statsWithIcon = actionTooltip.wandAction().getTooltipShowables();
        }


        @Override
        public int getHeight() {
            return (statsWithIcon.size()) * 12;
        }

        @Override
        public int getWidth(@NotNull Font pFont) {
            return statsWithIcon.entrySet().stream()
                    .mapToInt(entry -> pFont.width(translated(entry.getKey(), entry.getValue())))
                    .max()
                    .orElse(0);
        }
        private Component translated(AbstractWandAction.TooltipShowableStats stat, String value) {
            return Component.translatable(stat.getTranslationKey(), value);
        }

        @Override
        public void renderText(@NotNull Font pFont, int pMouseX, int pMouseY, @NotNull Matrix4f pMatrix, MultiBufferSource.@NotNull BufferSource pBufferSource) {
            int pY = 0;
            for (Map.Entry<AbstractWandAction.TooltipShowableStats, String> entry : statsWithIcon.entrySet()) {
                pFont.drawInBatch(translated(entry.getKey(), entry.getValue()), pMouseX + 12, pMouseY + pY, -1, false, pMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY += pFont.lineHeight + 2;
            }
        }

        @Override
        public void renderImage(Font pFont, int pMouseX, int pMouseY, @NotNull GuiGraphics pGuiGraphics) {
            int pY = 0;
            int lineH = pFont.lineHeight + 2;
            //ClientTooltipComponent.super.renderImage(pFont, pMouseX, pMouseY, pGuiGraphics);
            for (AbstractWandAction.TooltipShowableStats icon : statsWithIcon.keySet()) {
                pGuiGraphics.blit(icon.getIcon(), pMouseX, pMouseY + pY,  8, 8,0,0,8,8,8,8);
                pY += lineH;
            }
            //pY += lineH;
            //pGuiGraphics.blit(INFO_ICON, pMouseX, pMouseY + pY,  8, 8,0,0,8,8,8,8);
        }
    }

}
