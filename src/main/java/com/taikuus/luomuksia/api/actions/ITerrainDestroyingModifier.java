package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.capability.TerrainDestroyingProgression;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.setup.CapAndAttachmentRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public interface ITerrainDestroyingModifier extends IOnBounceModifier, IOnHitModifier {
    default float getTerrainDestroyingValue(AbstractModifiableProj proj, HitResult result) {
        return 0F;
    }
    @Override
    default void onBounce(AbstractModifiableProj proj, HitResult result, Direction projFacing) {
        IOnBounceModifier.super.onBounce(proj, result, projFacing);
        if (result instanceof BlockHitResult blockHitResult) {
            tryDestroyBlock(proj, blockHitResult);
        }
    }
    @Override
    default void onHit(AbstractModifiableProj proj, HitResult result) {
        IOnHitModifier.super.onHit(proj, result);
        if (result instanceof BlockHitResult blockHitResult) {
            tryDestroyBlock(proj, blockHitResult);
        }
    }
    default void tryDestroyBlock(AbstractModifiableProj proj, BlockHitResult result) {
        Entity entity = proj.getOwner();
        if (entity instanceof Player player) {
            TerrainDestroyingProgression terrainDestroyingProgression = player.getCapability(CapAndAttachmentRegistry.TERRAIN_DESTROYING_CAP);
            if (terrainDestroyingProgression != null) {
                float terrainDestroyingValue = getTerrainDestroyingValue(proj, result);
                if (terrainDestroyingValue > 0) {
                    terrainDestroyingProgression.tryAddAccumulationAndBreak(result.getBlockPos(), proj.level(), terrainDestroyingValue);
                }
            }
        }
    }
}
