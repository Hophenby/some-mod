package com.taikuus.luomuksia.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileLightBlade;
import com.taikuus.luomuksia.utils.TrailingPath3D;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ProjRendererLightBlade extends EntityRenderer<ProjectileLightBlade> {
    //private final ItemRenderer dummyRenderer;
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(RegistryNames.getRL("c2"), "main");
    private final ModelPart body;
    public ProjRendererLightBlade(EntityRendererProvider.Context pContext) {
        super(pContext);
        //this.dummyRenderer = pContext.getItemRenderer();
        ModelPart root = pContext.bakeLayer(LAYER_LOCATION);
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, -4.0F, -0.5F, 1.0F, 31.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ProjectileLightBlade pEntity) {
        return ResourceLocation.withDefaultNamespace("textures/block/beacon.png");// to be replaced
    }

    @Override
    public void render(ProjectileLightBlade pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight) {
        //we will check if the logic part of this proj works perfectly before we start implementing the rendering part
        //only render the point of the blade
        TrailingPath3D trail = pEntity.getTrail();
        TrailingPath3D.PairedIterator trailIterator = trail.getPairedIterator(pEntity.getValidTrailAfterWhen() + 1);
        while (trailIterator.hasNext()) {
            TrailingPath3D.Vec3TPair currentPart = trailIterator.next();
            Vec3 head = currentPart.head().vec();
            Vec3 tail = currentPart.tail().vec();
            Vec3 diff = head.subtract(tail);
//            float yRot = (float) Math.toDegrees(Math.atan2(diff.z(), diff.x()));
//            {
//                pPoseStack.pushPose();
//                pPoseStack.scale(2f, 2f, 2f);
//                pPoseStack.translate(-pEntity.getX(), -pEntity.getY(), -pEntity.getZ());
//                pPoseStack.translate(tail.x(), tail.y(), tail.z());
//                this.dummyRenderer.renderStatic(Items.ACACIA_LOG.getDefaultInstance(), ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, pEntity.level(), pEntity.getId());
//                pPoseStack.popPose();
//            }
            {
                pPoseStack.pushPose();
                pPoseStack.rotateAround(new Quaternionf().rotateTo(new Vector3f(0, 1f, 1e-5f), diff.toVector3f()),0, 0, 0);
                pPoseStack.scale(2f, (float) (diff.length() * 16 / 31), 2f);
                pPoseStack.translate(0, 0.5f, 0);
                body.render(pPoseStack,
                        pBufferSource.getBuffer(RenderType.beaconBeam(getTextureLocation(pEntity), false)),
                        pPackedLight, OverlayTexture.NO_OVERLAY, 0x8a5aefc3);
                pPoseStack.popPose();
            }
        }
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBufferSource, pPackedLight);
    }
}
