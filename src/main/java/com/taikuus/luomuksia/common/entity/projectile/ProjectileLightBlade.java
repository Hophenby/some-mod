package com.taikuus.luomuksia.common.entity.projectile;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.utils.TrailingPath3D;
import com.taikuus.luomuksia.setup.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

/**
 * A projectile that is a laser blade.
 * It is a projectile with extreme short existence time
 * its length is determined by the speed of the projectile
 */
public class ProjectileLightBlade extends AbstractModifiableProj {

    private final TrailingPath3D trail = new TrailingPath3D();
    private static final EntityDataAccessor<TrailingPath3D> DATA_TRAIL = SynchedEntityData.defineId(ProjectileLightBlade.class, EntityRegistry.TRAIL_DATA.get());
    private final static int trailExistTime = 3;
    public ProjectileLightBlade(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ProjectileLightBlade(Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(EntityRegistry.PROJECTILE_LIGHT_BLADE.get(), pOwner, pX, pY, pZ, pLevel);
        setMaxExistingTicks(3);
        setDamage(getProjBoundDamage() + 1.0f);
        critFactor += 0.06f;
        this.setNoGravity(true);
        this.noPhysics = true;
        setDynamicLightLevel(14);
        this.setPiercing(true);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TRAIL, new TrailingPath3D());
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        tag.put("trail", trail.toNBT());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        trail.clear();
        trail.addAll(TrailingPath3D.fromNBT(tag.getCompound("trail")));
    }
    @Override
    public void tick(){
        super.tick();
        if (true){
            trail.add(new TrailingPath3D.Vec3WithTime(getEyePosition(), getTimer()));
            if (!level().isClientSide) {
                AABB aabb = trail.getBoundingBoxAfterWhen(getValidTrailAfterWhen());
                if (aabb != null) setBoundingBox(aabb);
            }
        }
        //Luomuksia.LOGGER.info("trail: " + trail);
    }

    @Override
    public boolean canCollideWith(@NotNull Entity entity) {
        return super.canCollideWith(entity) && trail.collideWith(entity.getBoundingBox(), getTimer() - trailExistTime);
    }
    public int getValidTrailAfterWhen(){
        return getTimer() - trailExistTime - 1;
    }

    public TrailingPath3D getTrail() {
        return trail;
    }

//    @Override
//    public @NotNull AABB getBoundingBoxForCulling() {
//        AABB aabb = trail.getBoundingBox(getValidTrailAfterWhen());
//        return aabb == null ? super.getBoundingBoxForCulling() : aabb;
//    }
}
