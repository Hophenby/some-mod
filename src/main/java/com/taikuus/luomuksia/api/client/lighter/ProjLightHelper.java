package com.taikuus.luomuksia.api.client.lighter;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

/**
 * This code may be taken from LambDynamicLights, an MIT fabric mod: <a href="https://github.com/LambdAurora/LambDynamicLights">Github Link</a>
 * <p></p>
 * Since mod Ars Nouveau added the lamb's dynamic light system, we can use it to make the projectile a light source (not completed).
 * But we should implement the logic ourselves when Ars Nouveau is not loaded.
 * <p></p>
 * the code only plans to work with our own AbstractModifiableProj, so it is not a complete implementation.
 * @author LambdAurora
 * @version 1.3.3
 */
public class ProjLightHelper {
    private final AbstractModifiableProj projLitFor;
    private double lastX;
    private double lastY;
    private double lastZ;
    private int lastLightLevel;
    private LongOpenHashSet trackedLitChunkPos = new LongOpenHashSet();

    public ProjLightHelper(AbstractModifiableProj projLitFor) {
        this.projLitFor = projLitFor;
        this.lastX = projLitFor.getX();
        this.lastY = projLitFor.getEyeY();
        this.lastZ = projLitFor.getZ();
        this.lastLightLevel = getDynamicLightLevel();
    }

    public void setDynamicLightLevel(int dynamicLightLevel){

        this.projLitFor.getEntityData().set(AbstractModifiableProj.LIGHT_LEVEL, dynamicLightLevel);
        //Luomuksia.LOGGER.debug("setDynamicLightLevel() " + getDynamicLightLevel());
    }

    public int getDynamicLightLevel() {
        return this.projLitFor.getEntityData().get(AbstractModifiableProj.LIGHT_LEVEL);
    }

    public boolean updateDynamicLight(LevelRenderer renderer) {
        if (getDynamicLightLevel() < 0)
            return false;
        var dX = this.projLitFor.getX() - this.lastX;
        var dY = this.projLitFor.getEyeY() - this.lastY;
        var dZ = this.projLitFor.getZ() - this.lastZ;
        if (dX * dX + dY * dY + dZ * dZ < 0.3 && this.lastLightLevel == getDynamicLightLevel() && getDynamicLightLevel() > 0) {
            return false;
        }
        this.lastX = this.projLitFor.getX();
        this.lastY = this.projLitFor.getEyeY();
        this.lastZ = this.projLitFor.getZ();
        this.lastLightLevel = getDynamicLightLevel();

        var newPos = new LongOpenHashSet();

        var entityChunkPos = this.projLitFor.chunkPosition();
        var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, Mth.floor((long) (this.projLitFor.getEyeY()) >> 4), entityChunkPos.z);

        // update the chunk the entity is in
        ProjLightUtils.scheduleChunkRebuild(renderer, chunkPos);
        ProjLightUtils.updateTrackedChunks(chunkPos, this.trackedLitChunkPos, newPos);

        var directionX = (this.projLitFor.blockPosition().getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
        var directionY = (Mth.floor(this.projLitFor.getEyeY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
        var directionZ = (this.projLitFor.blockPosition().getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

        // update surrounding chunks
        for (int i = 0; i < 7; i++) {
            if (i % 4 == 0) {
                chunkPos.move(directionX); // X
            } else if (i % 4 == 1) {
                chunkPos.move(directionZ); // XZ
            } else if (i % 4 == 2) {
                chunkPos.move(directionX.getOpposite()); // Z
            } else {
                chunkPos.move(directionZ.getOpposite()); // origin
                chunkPos.move(directionY); // Y
            }
            ProjLightUtils.scheduleChunkRebuild(renderer, chunkPos);
            ProjLightUtils.updateTrackedChunks(chunkPos, this.trackedLitChunkPos, newPos);
        }
        // Schedules the rebuild of removed chunks.
        this.scheduleTrackedChunksRebuild(renderer);
        // Update tracked lit chunks.
        this.trackedLitChunkPos = newPos;
        return true;
    }

    public LongOpenHashSet getCopiedTrackLitChunkPos() {
        return new LongOpenHashSet(this.trackedLitChunkPos);
    }

    public void scheduleTrackedChunksRebuild(LevelRenderer renderer) {
        if (Minecraft.getInstance().level == this.projLitFor.level())
            for (long pos : this.trackedLitChunkPos) {
                ProjLightUtils.scheduleChunkRebuild(renderer, pos);
            }
    }
}
