package com.taikuus.luomuksia.mixin.client;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.api.client.lighter.ProjLightUtils;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity>  {

    @Inject(method = "getBlockLightLevel", at = @At("RETURN"), cancellable = true)
    private void onGetBlockLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof AbstractModifiableProj proj) {
            int vanilla = cir.getReturnValueI();
            int entityLuminance = proj.getDynamicLightLevel();
            if (entityLuminance >= 15)
                cir.setReturnValue(entityLuminance);

            int posLuminance = (int) ProjLightUtils.getDynamicLightLevel(pos);

            cir.setReturnValue(Math.max(Math.max(vanilla, entityLuminance), posLuminance));
        }else {
            int posLuminance = (int) ProjLightUtils.getDynamicLightLevel(pos);
            cir.setReturnValue(Math.max(cir.getReturnValueI(), posLuminance));
        }
    }
}
