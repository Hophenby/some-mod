package com.taikuus.luomuksia.common.entity.projectile;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.utils.ProjUtils;
import com.taikuus.luomuksia.api.utils.Vec3List;
import com.taikuus.luomuksia.setup.EntityRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A projectile that is a laser blade.
 * It is a projectile with extreme short existence time and extreme speed.
 * It may use a linked list of points to draw the blade and check for collisions.
 */
public class ProjectileLightBlade extends AbstractModifiableProj {

    public static final EntityDataAccessor<Vec3List> BLADE_POINTS = SynchedEntityData.defineId(ProjectileLightBlade.class, EntityRegistry.LINKED_VEC3_LIST.get());
    protected ProjectileLightBlade(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected ProjectileLightBlade(EntityType<? extends AbstractModifiableProj> pEntityType, Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pOwner, pX, pY, pZ, pLevel);
        this.maxExistingTicks = 3;
    }
    private Vec3 bladeStart;
    private Vec3 bladeEnd;
    private boolean bladeEndFlag = false;
    public Vec3List getBladePoints() {
        return this.entityData.get(BLADE_POINTS);
    }
    public void setBladePoints(Vec3List list) {
        this.entityData.set(BLADE_POINTS, list);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(BLADE_POINTS, new Vec3List());
    }

    @Override
    public void tick() {
        super.tick();
        if (timer % 2 == 0 && !this.bladeEndFlag) {
            var blade = this.getBladePoints();
            blade.add(new Vec3(this.getX(), this.getY(), this.getZ()));
            this.setBladePoints(blade);
            this.bladeEnd = new Vec3(this.getX(), this.getY(), this.getZ());
        }
    }

    @Override
    public void traceAnyHit(@Nullable HitResult rayTraceResult, Vec3 thisPosition, Vec3 nextPosition) {
        List<Entity> entities = this.getEntitiesOnBlade();
        for (Entity entity : entities) {
            if (entity == this) {
                continue;
            }
            this.onHit(new EntityHitResult(entity));
        }

        if (rayTraceResult != null && rayTraceResult.getType() != HitResult.Type.MISS) {
            this.bladeEnd = rayTraceResult.getLocation();
            this.bladeEndFlag = true;
            this.onHit(rayTraceResult);
        }
    }

    /**
     * Get the hit result of the projectile.
     * @return The hit result of the projectile.
     */
    @Override
    public HitResult getHitResult() {
        var blade = this.getBladePoints();
        if (blade.size() < 2) {
            return super.getHitResult();
        }
        Iterator<Vec3> iterator = blade.iterator();
        Vec3 head = iterator.next();
        Vec3 tail;
        while (iterator.hasNext()) {
            tail = iterator.next();
            HitResult hitResult = this.level().clip(new ClipContext(head, tail, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            if (hitResult.getType() != HitResult.Type.MISS) {
                return hitResult;
            }
            head = tail;
        }
        return null;
    }
    private List<Entity> getEntitiesOnBlade() {
        var blade = this.getBladePoints();
        if (blade.size() < 2) {
            return new ArrayList<>();
        }
        Iterator<Vec3> iterator = blade.iterator();
        Vec3 head = iterator.next();
        Vec3 tail;
        List<Entity> entities = new ArrayList<>();
        while (iterator.hasNext()) {
            tail = iterator.next();
            entities.addAll(ProjUtils.getEntitiesOnLine(this.level(), head, tail, 0.0, this.piercing));
            head = tail;
        }
        return entities;
    }
}
