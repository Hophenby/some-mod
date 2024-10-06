package com.taikuus.luomuksia.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.taikuus.luomuksia.api.utils.Vec3List;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileLightBlade;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class ProjRendererLightBlade extends EntityRenderer<ProjectileLightBlade> {
    public ProjRendererLightBlade(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ProjectileLightBlade pEntity) {
        return ResourceLocation.parse("minecraft:missingno");// to be replaced
    }

    @Override
    public void render(ProjectileLightBlade pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {
        Vec3List blade = pEntity.getBladePoints();
        Vec3List.PairedIterartor bladeIter = blade.getPairedIterator();
        while (bladeIter.hasNext()){
            pPoseStack.pushPose();
            Vec3List.Vec3Pair pair = bladeIter.next();
            // convert the vector to quaternion matrix
            Quaternionf quaternion = new Quaternionf().rotateTo(pair.head().toVector3f(), pair.tail().toVector3f());

            // rotate the blade segment
            pPoseStack.translate(pair.head().x, pair.head().y, pair.head().z);
            pPoseStack.mulPose(quaternion);
            pPoseStack.translate(-pair.head().x, -pair.head().y, -pair.head().z);

            // render the blade segment
            // each segment is a line from pair.head to pair.tail
            // use beacon beam renderer as reference
            BeaconRenderer.renderBeaconBeam(
                    pPoseStack,
                    pBufferSource,
                    getTextureLocation(pEntity),
                    pPartialTick,
                    1.0F,
                    pEntity.level().getGameTime(),
                    0,
                    256,
                    0x88ff88,
                    0.04f,
                    0.2f
            );
            pPoseStack.popPose();
        }

    }
}
