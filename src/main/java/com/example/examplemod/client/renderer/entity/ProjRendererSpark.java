package com.example.examplemod.client.renderer.entity;

import com.example.examplemod.common.entity.projectile.ProjectileSpark;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ProjRendererSpark extends EntityRenderer<ProjectileSpark> {
    private final ResourceLocation entityTexture;

    public ProjRendererSpark(EntityRendererProvider.Context renderManagerIn, ResourceLocation entityTexture) {
        super(renderManagerIn);
        this.entityTexture = entityTexture;

    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ProjectileSpark pEntity) {
        return entityTexture;
    }
}
