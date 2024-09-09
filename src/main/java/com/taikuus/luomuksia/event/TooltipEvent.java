package com.taikuus.luomuksia.event;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.client.tooltip.ComponentTooltip;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Luomuksia.MODID)
public class TooltipEvent {
    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        var itemStack = event.getItemStack();
        if (!itemStack.getComponents().isEmpty() && event.getFlags().isAdvanced() && event.getFlags().hasShiftDown()) {
            ComponentTooltip.modifyTooltip(itemStack, event.getEntity(), event.getToolTip(), event.getFlags(), event.getContext());
        }
    }
}
