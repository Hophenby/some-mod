package com.taikuus.luomuksia.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.taikuus.luomuksia.api.utils.Vec3List;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileLightBlade;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

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

        int skyLight = pEntity.level().getBrightness(LightLayer.SKY, pEntity.blockPosition());
        int blockLight = pEntity.getDynamicLightLevel();
        int packedLight = getPackedLightmap(skyLight, blockLight);

        while (bladeIter.hasNext()){
            pPoseStack.pushPose();
            // render the blade segment
            // each segment is a line from pair.head to pair.tail
            Vec3List.Vec3Pair pair = bladeIter.next();
            renderBlade(
                    pPoseStack,
                    pBufferSource,
                    this.getTextureLocation(pEntity),
                    1.0f,
                    0x00FF00,
                    packedLight,
                    pair.head().toVector3f(),
                    pair.tail().toVector3f()
            );
            pPoseStack.popPose();
        }

    }
    private static int getPackedLightmap(int sky, int block) {
        return sky << 20 | block << 4;
    }
    private static void addVertex(
            int packedLightMap, PoseStack.Pose pPose, VertexConsumer pConsumer, int pColor, float pX, float pY, float pZ, float pU, float pV
    ) {
        pConsumer.addVertex(pPose, pX, pY, pZ)
                .setColor(pColor)
                .setUv(pU, pV)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLightMap)
                .setNormal(pPose, 0.0F, 1.0F, 0.0F);
    }
    private static void renderQuad(
            int pLightMap,
            PoseStack.Pose pPose,
            VertexConsumer pConsumer,
            int pColor,
            Vector3f pHalfParellel,
            Vector3f pPerpendicular,
            Vector3f pMidBottom,
            float pMinU,
            float pMaxU,
            float pMinV,
            float pMaxV
    ) {
        Vector3f quadP1 = new Vector3f(pMidBottom).add(pHalfParellel);
        Vector3f quadP2 = new Vector3f(pMidBottom).sub(pHalfParellel);
        Vector3f quadP3 = new Vector3f(quadP2).add(pPerpendicular);
        Vector3f quadP4 = new Vector3f(quadP1).add(pPerpendicular);

        addVertex(pLightMap, pPose, pConsumer, pColor, quadP1.x(), quadP1.y(), quadP1.z(), pMaxU, pMinV);
        addVertex(pLightMap, pPose, pConsumer, pColor, quadP2.x(), quadP2.y(), quadP2.z(), pMinU, pMinV);
        addVertex(pLightMap, pPose, pConsumer, pColor, quadP3.x(), quadP3.y(), quadP3.z(), pMinU, pMaxV);
        addVertex(pLightMap, pPose, pConsumer, pColor, quadP4.x(), quadP4.y(), quadP4.z(), pMaxU, pMaxV);
    }

    private static void renderBladePart(
            int pLightMap,
            PoseStack pPoseStack,
            VertexConsumer pConsumer,
            int pColor,
            Vector3f pHead,
            Vector3f pTail,
            Vector3f pBladeDiameter,
            int pBladePolygon,
            float pMinU,
            float pMaxU,
            float pMinV,
            float pMaxV
    ) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        for (int i = 0; i < pBladePolygon; i++) {
            float angle = (float) (i * Math.PI * 2 / pBladePolygon);
            //float nextAngle = (float) ((i + 1) * Math.PI * 2 / pBladePolygon);
            Vector3f halfParellel = new Vector3f(pBladeDiameter).mul((float) Math.cos(angle));
            Vector3f perpendicular = new Vector3f(pBladeDiameter).mul((float) Math.sin(angle));
            //Vector3f nextHalfParellel = new Vector3f(pBladeDiameter).mul((float) Math.cos(nextAngle));
            //Vector3f nextPerpendicular = new Vector3f(pBladeDiameter).mul((float) Math.sin(nextAngle));
            Vector3f midBottom = new Vector3f(pHead).lerp(pTail, (float) i / pBladePolygon);
            Vector3f nextMidBottom = new Vector3f(pHead).lerp(pTail, (float) (i + 1) / pBladePolygon);
            renderQuad(pLightMap, posestack$pose, pConsumer, pColor, halfParellel, perpendicular, midBottom, pMinU, pMaxU, pMinV, pMaxV);
            //renderQuad(pLightMap, posestack$pose, pConsumer, pColor, nextHalfParellel, nextPerpendicular, nextMidBottom, pMinU, pMaxU, pMinV, pMaxV);
        }
    }

    private static void renderBlade(
            PoseStack pPoseStack,
            MultiBufferSource pBufferSource,
            ResourceLocation pBladeLocation,
            float pTextureScale,
            int pColor,
            int pLightMap,
            Vector3f pHead,
            Vector3f pTail){
        pPoseStack.pushPose();
        pPoseStack.translate(pHead.x(), pHead.y(), pHead.z());
        VertexConsumer vertexConsumer = pBufferSource.getBuffer(RenderType.beaconBeam(pBladeLocation, false));

        Vector3f headToTail = new Vector3f(pTail).sub(pHead);
        Vector3f bladeDiameter = new Vector3f(0.1f, 0.1f, 0.1f);
        int bladePolygon = 3;

        renderBladePart(
                pLightMap,
                pPoseStack,
                vertexConsumer,
                pColor,
                new Vector3f(0.0f, 0.0f, 0.0f),
                headToTail,
                bladeDiameter,
                bladePolygon,
                0.0f,
                pTextureScale,
                0.0f,
                1.0f);

        pPoseStack.popPose();

    }
}
