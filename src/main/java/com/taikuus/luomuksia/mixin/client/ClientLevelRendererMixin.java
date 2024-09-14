package com.taikuus.luomuksia.mixin.client;

import com.taikuus.luomuksia.api.client.lighter.ProjLightUtils;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class ClientLevelRendererMixin {
    @Inject(
            method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I",
            at = @At("TAIL"),
            cancellable = true
    )
    private static void onGetLightmapCoordinates(BlockAndTintGetter world, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!world.getBlockState(pos).isSolidRender(world, pos)) {
            cir.setReturnValue(ProjLightUtils.getLightmapWithDynamicLight(ProjLightUtils.getDynamicLightLevel(pos), cir.getReturnValue()));
        }
    }
}
