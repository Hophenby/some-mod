package com.example.examplemod.event;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.tooltip.ComponentTooltip;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class TooltipEvent {
    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        var itemStack = event.getItemStack();
        if (!itemStack.getComponents().isEmpty() && event.getFlags().isAdvanced() && event.getFlags().hasShiftDown()) {
            ComponentTooltip.modifyTooltip(itemStack, event.getEntity(), event.getToolTip(), event.getFlags(), event.getContext());
        }
    }
}
