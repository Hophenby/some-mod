package com.taikuus.luomuksia.api.client.lighter;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Since mod Ars Nouveau added the lamb's dynamic light system, we can use it to make the projectile a light source (not completed). <p></p>
 * But we should implement the logic ourselves when Ars Nouveau is not loaded.
 * <p></p>
 * the code only plans to work with our own AbstractModifiableProj, so it is not a complete implementation.
 */
public class ProjLightUtils {
    private static final Set<AbstractModifiableProj> affectedProjs = new HashSet<>();
    /**
     * The lock for the set of projectiles that are affected by this modifier.
     */
    private static final ReentrantReadWriteLock lightSourcesLock = new ReentrantReadWriteLock();
    public static void addLightSource(AbstractModifiableProj proj) {
        if (!proj.level().isClientSide()) {
            return;
        }
        lightSourcesLock.writeLock().lock();
        affectedProjs.add(proj);
        lightSourcesLock.writeLock().unlock();
        //Luomuksia.LOGGER.debug("Added light source: " + proj + " count: " + affectedProjs.size());
    }
    public boolean isLightSource(AbstractModifiableProj proj) {
        return affectedProjs.contains(proj);
    }
    /**
     * Schedules a chunk rebuild at the specified chunk position.
     *
     * @param renderer the renderer
     * @param chunkPos the chunk position
     */
    public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, @NotNull BlockPos chunkPos) {
        scheduleChunkRebuild(renderer, chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
    }
    /**
     * Schedules a chunk rebuild at the specified chunk position.
     *
     * @param renderer the renderer
     * @param chunkPos the packed chunk position
     */
    public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, long chunkPos) {
        scheduleChunkRebuild(renderer, BlockPos.getX(chunkPos), BlockPos.getY(chunkPos), BlockPos.getZ(chunkPos));
    }
    /**
     * Updates the tracked chunk sets.
     *
     * @param chunkPos the packed chunk position
     * @param old      the set of old chunk coordinates to remove this chunk from it
     * @param newPos   the set of new chunk coordinates to add this chunk to it
     */
    public static void updateTrackedChunks(@NotNull BlockPos chunkPos, @Nullable LongOpenHashSet old, @Nullable LongOpenHashSet newPos) {
        if (old != null || newPos != null) {
            long pos = chunkPos.asLong();
            if (old != null)
                old.remove(pos);
            if (newPos != null)
                newPos.add(pos);
        }
    }
    public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, int x, int y, int z) {
        if (Minecraft.getInstance().level != null) renderer.setSectionDirty(x, y, z);
    }
    /**
     * Returns the lightmap with combined light levels.
     *
     * @param dynamicLightLevel the dynamic light level
     * @param lightmap          the vanilla lightmap coordinates. See {@link net.minecraft.client.renderer.LightTexture}
     *                          or <a href="https://wiki.vg/Chunk_Format#Block_Light">Chunk Format</a>
     * @return the modified lightmap coordinates
     */
    public static int getLightmapWithDynamicLight(double dynamicLightLevel, int lightmap) {
        if (dynamicLightLevel > 0) {
            // lightmap is (skyLevel << 20 | blockLevel << 4)

            // Get vanilla block light level.
            int blockLevel = getBlockLightNoPatch(lightmap);
            if (dynamicLightLevel > blockLevel) {
                // Equivalent to a << 4 bitshift with a little quirk: this one ensure more precision (more decimals are saved).
                int luminance = (int) (dynamicLightLevel * (double) (0xf + 1));
                //Luomuksia.LOGGER.debug("luminance: " + luminance);
                lightmap &= 0xfff00000;
                lightmap |= luminance & 0x000fffff;
            }
        }
        return lightmap;
    }
    // Reverts the forge patch to LightTexture.block
    public static int getBlockLightNoPatch(int light) {
        return light >> 4 & '\uffff';
    }
    /**
     * Updates all light sources.
     *
     * @param renderer the renderer
     */
    public static void updateAll(LevelRenderer renderer) {


        long lastUpdate = System.currentTimeMillis();
        int lastUpdateCount = 0;

        lightSourcesLock.readLock().lock();
        for (AbstractModifiableProj lightSource : affectedProjs) {
            if (lightSource.getLighter().updateDynamicLight(renderer)) {
                lastUpdateCount++;
            }
        }
        //Luomuksia.LOGGER.debug("Updated " + lastUpdateCount + " light sources in " + (System.currentTimeMillis() - lastUpdate) + "ms.");
        lightSourcesLock.readLock().unlock();

    }
    /**
     * Returns the dynamic light level at the specified position.
     *
     * @param pos the position
     * @return the dynamic light level at the specified position
     */
    public static double getDynamicLightLevel(@NotNull BlockPos pos) {
        double result = 0;
        lightSourcesLock.readLock().lock();
        for (var lightSource : affectedProjs) {
            result = maxDynamicLightLevel(pos, lightSource, result);
        }
        lightSourcesLock.readLock().unlock();
        //if (result > 0) Luomuksia.LOGGER.debug("Dynamic light level at " + pos + ": " + result);
        return Mth.clamp(result, 0, 15);
    }
    private static final double MAX_RADIUS = 7.75;
    private static final double MAX_RADIUS_SQUARED = MAX_RADIUS * MAX_RADIUS;
    /**
     * Returns the dynamic light level generated by the light source at the specified position.
     *
     * @param pos               the position
     * @param lightSource       the light source
     * @param currentLightLevel the current surrounding dynamic light level
     * @return the dynamic light level at the specified position
     */
    public static double maxDynamicLightLevel(@NotNull BlockPos pos, @NotNull AbstractModifiableProj lightSource, double currentLightLevel) {
        int luminance = lightSource.getDynamicLightLevel();
        if (luminance >= 0) {
            // This is only a rough process to determine the light level.
            double dx = pos.getX() - lightSource.getEyePosition().x + 0.5;
            double dy = pos.getY() - lightSource.getEyePosition().y + 0.5;
            double dz = pos.getZ() - lightSource.getEyePosition().z + 0.5;

            double distanceSquared = dx * dx + dy * dy + dz * dz;
            // 7.75 because they say 7.75 is a lucky number.
            // 15 (max range for blocks) would be too much and a bit unlucky.
            if (distanceSquared <= MAX_RADIUS_SQUARED) {
                double multiplier = 1.0 - Math.sqrt(distanceSquared) / MAX_RADIUS;
                double lightLevel = multiplier * luminance;
                if (lightLevel > currentLightLevel) {
                    return lightLevel;
                }
            }
        }
        return currentLightLevel;
    }
    public static void removeLightSource(AbstractModifiableProj proj) {
        lightSourcesLock.writeLock().lock();
        var sourceIterator = affectedProjs.iterator();
        AbstractModifiableProj it;
        while (sourceIterator.hasNext()) {
            it = sourceIterator.next();
            if (it.equals(proj)) {
                sourceIterator.remove();
            }
        }
        lightSourcesLock.writeLock().unlock();

    }
    public static void clearLightSources() {
        lightSourcesLock.writeLock().lock();
        affectedProjs.clear();
        lightSourcesLock.writeLock().unlock();
    }
    public static void checkLightSources() {
        lightSourcesLock.writeLock().lock();
        affectedProjs.removeIf(Entity::isRemoved);
        lightSourcesLock.writeLock().unlock();
    }
}
