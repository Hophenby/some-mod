package com.taikuus.luomuksia.client.tooltip;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.ActionCardDeck;
import com.taikuus.luomuksia.api.wand.WandData;
import com.taikuus.luomuksia.api.wand.WrappedWandAction;
import com.taikuus.luomuksia.api.wand.wandattr.WandAttr;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public record WandTooltip(WandData wandData) implements TooltipComponent {
    public static class ClientWandTooltip implements ClientTooltipComponent{
        private final WandData wandData;
        private final Map<Holder<WandAttr>, Component> tooltips;
        private final int wandSize;
        private static final ResourceLocation WAND_EMPTY_SLOT = RegistryNames.getRL("textures/wand_attr/empty_slot.png");

        public ClientWandTooltip(WandTooltip wandTooltip) {
            this.wandData = wandTooltip.wandData().copy();
            this.tooltips = wandData.getAttributes().entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().getTooltip()))
                    .filter(entry -> !entry.getKey().value().isHidden())
                    .collect(Object2ObjectOpenHashMap::new, (a, b) -> a.put(b.getKey(), b.getValue()), Map::putAll);
            this.wandSize = wandData.getWandSize();
        }

        @Override
        public int getHeight() {
            return (wandSize % 9 == 0 ? wandSize / 9 : wandSize / 9 + 1) * 18 + tooltips.size() * 12;
        }

        @Override
        public int getWidth(Font pFont) {
            int textMax = tooltips.values().stream().max(Comparator.comparingInt(pFont::width)).map(pFont::width).orElse(0) + 16;
            int actionsMax = Math.min(wandSize, 9) * 18;
            return Math.max(textMax, actionsMax);
        }

        @Override
        public void renderText(@NotNull Font pFont, int pMouseX, int pMouseY, @NotNull Matrix4f pMatrix, MultiBufferSource.@NotNull BufferSource pBufferSource) {
            int pY = 0;
            for (Component component : tooltips.entrySet().stream().sorted(
                    Comparator.comparingDouble(entry -> entry.getKey().value().getTooltipPriority())).map(Map.Entry::getValue).toList()) {
                pFont.drawInBatch(component, pMouseX + 12, pMouseY + pY, -1, false, pMatrix, pBufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                pY += pFont.lineHeight + 2;
            }
            //ClientTooltipComponent.super.renderText(pFont, pMouseX, pMouseY, pMatrix, pBufferSource);
        }

        @Override
        public void renderImage(@NotNull Font pFont, int pMouseX, int pMouseY, @NotNull GuiGraphics pGuiGraphics) {
            int pY = 0;
            int lineH = pFont.lineHeight + 2;
            //ClientTooltipComponent.super.renderImage(pFont, pMouseX, pMouseY, pGuiGraphics);
            for (Holder<WandAttr> attr : tooltips.keySet()) {
                ResourceLocation iconRL = attr.value().getIconTexture();
                pGuiGraphics.blit(iconRL, pMouseX, pMouseY + pY,  8, 8,0,0,8,8,8,8);
                pY += lineH;
            }
            ActionCardDeck gathered = wandData.getDiscard().copy();
            gathered.draw(wandData.getDeck().copy());
            renderAction(gathered, pFont, pMouseX, pMouseY + pY, pGuiGraphics);
        }
        private void renderAction(ActionCardDeck deck, Font pFont, int pMouseX, int pMouseY, GuiGraphics pGuiGraphics) {
            List<ItemStack> toRender = new ArrayList<>();
            for (int i = 0; i < wandSize; i++) {
                WrappedWandAction wrapped = deck.get(i);
                ItemStack actionItemStack = wrapped == null ? ItemStack.EMPTY : wrapped.action().getActionItem().getDefaultInstance();
                toRender.add(actionItemStack);
            }
            for (int i = 0; i < toRender.size(); i++) {
                ItemStack stack = toRender.get(i);
                int x = pMouseX + (i % 9) * 18;
                int y = pMouseY + (i / 9) * 18 + 2;
                if (stack.isEmpty()) {
                    pGuiGraphics.blit(WAND_EMPTY_SLOT, x, y, 16, 16, 0, 0, 16, 16, 16, 16);
                }
                pGuiGraphics.renderItem(stack, x, y, i);
            }
        }

    }
}
