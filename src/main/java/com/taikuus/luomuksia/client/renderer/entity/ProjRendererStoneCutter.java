package com.taikuus.luomuksia.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileStoneCutter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static net.minecraft.world.level.block.Blocks.STONECUTTER;

public class ProjRendererStoneCutter extends EntityRenderer<ProjectileStoneCutter> {
    private final static ResourceLocation STONECUTTER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/atlas/blocks.png");
    private final BlockRenderDispatcher dispatcher;
    private float lastRot = 0.0f;
    private int lastTick = -1;

    public ProjRendererStoneCutter(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = context.getBlockRenderDispatcher();


    }
    public void render(ProjectileStoneCutter pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight){
        BlockState blockstate = STONECUTTER.defaultBlockState();
        Level level = pEntity.level();
        pPoseStack.pushPose();

        Vec3 projDir = pEntity.getDeltaMovement();

        //Rotation axis
        //We need to avoid the cross product of parallel vectors
        Vec3 yAxis = new Vec3(0.0, 1.0, 1E-7).normalize();
        Vec3 projDirRot = projDir.cross(yAxis);

        //Rotation angles
        double omega = projDir.length() * 24;

        // Check if the current tick is an integer tick
        int currentTick = pEntity.tickCount;
        if (currentTick != lastTick) {
            lastRot += Mth.lerp(pPartialTicks, 0, omega);
        }

        BlockPos blockpos = BlockPos.containing(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
        pPoseStack.rotateAround(new Quaternionf()
                        .rotateAxis(
                                (float) Math.toRadians(lastRot),
                                (float) projDirRot.x, (float) projDirRot.y, (float) projDirRot.z),
                0.0F, 0.2F, 0.0F);
        pPoseStack.translate(-0.5, 0.0, -0.5);
        var model = this.dispatcher.getBlockModel(blockstate);
        for (var renderType : model.getRenderTypes(blockstate, RandomSource.create(pEntity.maxExistingTicks), net.neoforged.neoforge.client.model.data.ModelData.EMPTY))
            this.dispatcher
                    .getModelRenderer()
                    .tesselateBlock(
                            level,
                            this.dispatcher.getBlockModel(blockstate),
                            blockstate,
                            blockpos,
                            pPoseStack,
                            pBuffer.getBuffer(net.neoforged.neoforge.client.RenderTypeHelper.getMovingBlockRenderType(renderType)),
                            false,
                            RandomSource.create(),
                            pEntity.maxExistingTicks,
                            OverlayTexture.NO_OVERLAY,
                            net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                            renderType
                    );
        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ProjectileStoneCutter pEntity) {
        return STONECUTTER_TEXTURE;
    }
}
