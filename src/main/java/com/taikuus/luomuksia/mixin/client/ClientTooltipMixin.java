package com.taikuus.luomuksia.mixin.client;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ClientTooltipMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Unique
    private boolean shouldShow = false;

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/TooltipFlag;isCreative()Z"))
    protected boolean forceVisible(TooltipFlag flag) {
        shouldShow = flag.isAdvanced() && flag.hasShiftDown(); // If the advanced tooltips are on and the shift key is pressed the method is run.
        return (flag.isAdvanced() && flag.hasShiftDown()) || flag.isCreative();
    }


}
