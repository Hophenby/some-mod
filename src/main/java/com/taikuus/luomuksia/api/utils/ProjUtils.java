package com.taikuus.luomuksia.api.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public class ProjUtils {
    @Nullable
    public static Vec3 findTarget(Vec3 start, Level world, double range, boolean targetingPlayers) {
        LivingEntity target = findEntityTarget(start, world, range, targetingPlayers);
        return target == null ? null : target.getEyePosition();
    }
    @Nullable
    public static LivingEntity findEntityTarget(Vec3 start, Level world, double range, boolean targetingPlayers) {
        AABB aabb = new AABB(start.x, start.y, start.z, start.x, start.y, start.z).inflate(range);
        return world.getEntities(null, aabb).stream()
                .filter(entity -> targetingPlayers ? entity instanceof LivingEntity : entity instanceof Mob)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity.isAlive() && isNoBlockBetween(world, start, entity.getEyePosition()))
                .min(Comparator.comparingDouble((v) -> start.distanceToSqr(v.getEyePosition())))
                .orElse(null);
    }

    /**
     * check if there is a block between start and end
     * @return true if there is no block between start and end
     */
    public static boolean isNoBlockBetween(Level world, Vec3 start, Vec3 end) {
        return world.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()

        )).getType() == HitResult.Type.MISS;
    }

    public static List<Entity> getEntitiesOnLine(Level world, Vec3 start, Vec3 end, double range, boolean targetingPlayers) {
        AABB aabb = new AABB(start.x, start.y, start.z, start.x, start.y, start.z).inflate(range);
        return world.getEntities(null, aabb).stream()
                .filter(entity -> targetingPlayers ? entity instanceof LivingEntity : entity instanceof Mob)
                .filter(entity -> entity.isAlive() && isEntityBetween(start, end, entity))
                .toList();
    }
    /**
     * check if there is the entity between start and end
     * @return true if there is no block between start and end
     */
    public static boolean isEntityBetween(Vec3 start, Vec3 end, Entity entity) {
        return Mth.rayIntersectsAABB(start, end, entity.getBoundingBox()) &&
                Mth.rayIntersectsAABB(end, start, entity.getBoundingBox());
    }
}
