package com.example.examplemod.api.entity;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.api.actions.IMotionModifier;
import com.example.examplemod.common.actions.modifier.AbstractModifierAction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModifiableProj extends Projectile implements IModifiableProj {
    public static final EntityDataAccessor<Integer> RED = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GREEN = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public final MotionModifiersHelper motionModifiersHelper = new MotionModifiersHelper();
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pLevel);
        setPos(pX, pY, pZ);
    }
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, Vec3 pos, Level pLevel){
        super(pEntityType, pLevel);
        setPos(pos.x, pos.y, pos.z);
    }
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, Entity pOwner, Level pLevel) {
        super(pEntityType,  pLevel);
        setOwner(pOwner);
    }
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pLevel);
        setOwner(pOwner);
        setPos(pX, pY, pZ);
    }

    /**
     * Define the data that should be synchronized between the client and server
     *
     * @param pBuilder
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(RED, 0);
        pBuilder.define(GREEN, 0);
        pBuilder.define(BLUE, 0);
        pBuilder.define(OWNER_ID, -1);
    }

    @Override
    public void tick() {
        Vec3 motion = getDeltaMovement();
        motion = motionModifiersHelper.applyHooks(motion);
        setDeltaMovement(motion);
        motionModifiersHelper.applyToProj(this);
        super.tick();
    }
    public void applyModifier(AbstractModifierAction modifier) {
        modifier.applyModifier(this);
    }
    @Override
    public void setOwner(@org.jetbrains.annotations.Nullable Entity pOwner) {
        super.setOwner(pOwner);
        if(pOwner != null) {
            this.entityData.set(OWNER_ID, pOwner.getId());
        }else{
            this.entityData.set(OWNER_ID, -1);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(OWNER_ID, tag.getInt("ownerId"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ownerId", this.entityData.get(OWNER_ID));
    }

    /**
     * Helper class to apply tickable motion modifiers to the projectile
     */
    public class MotionModifiersHelper {
        private final List<IMotionModifier> hookList = new ArrayList<IMotionModifier>();
        public void addHook(IMotionModifier hook) {
            hookList.add(hook);
        }
        public Vec3 applyHooks(Vec3 motion) {
            ExampleMod.LOGGER.debug("delta motion: " + motion.toString() + " position: " + position());
            if (hookList.isEmpty()) {
                return motion;
            }
            for (IMotionModifier hook : hookList) {
                motion = hook.applyPerTick(AbstractModifiableProj.this, motion);
            }
            return motion;
        }

        /**
         * Make the deltaMovement actually take effects
         */
        public void applyToProj(AbstractModifiableProj proj) {
            if (!proj.level().isClientSide){
                Vec3 newPos = proj.position().add(proj.getDeltaMovement());
                proj.setPos(newPos.x, newPos.y, newPos.z);
            }else {
                proj.setPos(proj.getX() + proj.getDeltaMovement().x, proj.getY() + proj.getDeltaMovement().y, proj.getZ() + proj.getDeltaMovement().z);
            }
        }
    }
}
