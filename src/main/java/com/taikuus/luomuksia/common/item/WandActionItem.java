package com.taikuus.luomuksia.common.item;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.client.tooltip.ActionTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class WandActionItem extends Item {
    private final AbstractWandAction action;
    public WandActionItem(Properties pProperties, AbstractWandAction action) {
        super(pProperties);
        this.action = action;
    }
    public AbstractWandAction getAction(){
        return action;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        if (pStack.getItem() instanceof WandActionItem item)
            return Optional.of(new ActionTooltip(item.getAction()));
        return super.getTooltipImage(pStack);
    }

    @Override
    public @NotNull Component getDescription() {
        return getAction().getDescription();
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack pStack, Item.@NotNull TooltipContext pContext, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.literal("   ").append(pTooltipFlag.hasShiftDown() ?
                getDescription() :
                Component.translatable("tooltip.action.luomuksia.shift").withStyle(ChatFormatting.GRAY)));
    }
}
