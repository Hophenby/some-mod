package com.taikuus.luomuksia.api.entity;

import com.taikuus.luomuksia.api.actions.IHookModifier;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.actions.IOnHitModifier;
import com.taikuus.luomuksia.common.actions.modifier.AbstractModifierAction;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.damagesource.DamageTypes.MOB_PROJECTILE;

public abstract class AbstractModifiableProj extends Projectile implements IModifiableProj {
    public int timer = 0;
    /**
     * The maximum number of ticks that the projectile can exist. This can be modified by some actions.
     */
    public int maxExistingTicks = 20 * 60; // 1 minute
    /**
     * The maximum number of "maximum number of ticks" that the projectile can exist.
     * This can NOT be modified by some actions. It is used to prevent the projectile from existing forever.
     */
    public int maxExistingTicksLimit = 20 * 3600; // an hour
    public boolean hitLiquid = false;
    public boolean hitBlock = false;
    public float damage = 0.0f;
    public float gravity = 0.03f;
    public float fricCoef = 0.97f;
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> RED = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GREEN = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> BLUE = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public final ModifiersHelper modifiersHelper = new ModifiersHelper();
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    protected AbstractModifiableProj(EntityType<? extends Projectile> pEntityType, Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        this(pEntityType, pLevel);
        setOwner(pOwner);
        setPos(pX, pY, pZ);
        maxExistingTicks = Math.min(maxExistingTicks, getMaxExistingTicksLimit());
    }
    public int getMaxExistingTicksLimit(){
        return maxExistingTicksLimit;
    }
    public int getMaxExistingTicks() {
        return maxExistingTicks;
    }
    public float getDamage() {
        return damage;
    }

    /**
     * Define the data that should be synchronized between the client and server
     *
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(RED, 0);
        pBuilder.define(GREEN, 0);
        pBuilder.define(BLUE, 0);
        pBuilder.define(OWNER_ID, -1);
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

    @Override
    public void tick() {
        timer++;
        if (!this.level().isClientSide && timer > getMaxExistingTicks()) {
            this.attemptRemoval();
            return;
        }

        Vec3 thisPosition = this.position();
        Vec3 nextPosition = getNextHitPosition();
        traceAnyHit(getHitResult(), thisPosition, nextPosition);

        Vec3 motion = getDeltaMovement();
        motion = modifiersHelper.applyMotiveHooks(motion);
        setDeltaMovement(motion);
        modifiersHelper.modifiedMove(this);
        super.tick();
    }
    @Override
    protected void onHit(HitResult result){
        result = transformHitResult(result);
        if (this.level().isClientSide) {
            return;
        }
        if (result instanceof EntityHitResult entityResult && this.getDamage() > 0){
            entityResult.getEntity().hurt(this.getDamageSource(), this.getDamage());
        }
        attemptRemoval();
    }
    public DamageSource getDamageSource() {
        return DamageUtil.source(this.level(), MOB_PROJECTILE, this.getOwner());
    }

    private void attemptRemoval() {
        this.modifiersHelper.applyHitHooks(getHitResult());
        this.remove(RemovalReason.DISCARDED);
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

    /**
     * The next position for ray tracing.
     * See {@link com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell#getNextHitPosition()}
     * @author baileyholl
     */
    public Vec3 getNextHitPosition() {
        return this.position().add(this.getDeltaMovement());
    }

    /**
     * See {@link com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell#traceAnyHit(HitResult, Vec3, Vec3)}
     * @author baileyholl
     */
    public void traceAnyHit(@Nullable HitResult raytraceresult, Vec3 thisPosition, Vec3 nextPosition) {
        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS) {
            nextPosition = raytraceresult.getLocation();
        }
        EntityHitResult entityraytraceresult = this.findHitEntity(thisPosition, nextPosition);
        if (entityraytraceresult != null) {
            raytraceresult = entityraytraceresult;
        }

        if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
            this.hasImpulse = true;
        }
    }
    public HitResult getHitResult() {
        Vec3 thisPosition = this.position();
        Vec3 nextPosition = getNextHitPosition();
        return this.level().clip(new ClipContext(thisPosition, nextPosition, hitLiquid ? ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, this));
    }
    /**
     * See {@link com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell#transformHitResult(HitResult)}
     * @author baileyholl
     */
    public @Nullable HitResult transformHitResult(@Nullable HitResult hitResult) {
        if (hitResult instanceof BlockHitResult hitResult1) {
            return new BlockHitResult(hitResult1.getLocation(), hitResult1.getDirection(), hitResult1.getBlockPos(), false);
        }
        return hitResult;
    }

    /**
     * See {@link com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell#findHitEntity(Vec3, Vec3)}
     * @author baileyholl
     */
    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, pStartVec, pEndVec, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
    }
    /**
     * Helper class to apply tickable motion modifiers to the projectile
     */
    public class ModifiersHelper {
        private final List<IHookModifier> motionHookList = new ArrayList<>();
        public void addHook(IHookModifier hook) {
            motionHookList.add(hook);
        }
        public Vec3 applyMotiveHooks(Vec3 motion) {
            motion = motion.add(0, -gravity, 0).multiply(fricCoef, fricCoef, fricCoef);
            if (motionHookList.isEmpty()) {
                return motion;
            }
            for (IHookModifier hook : motionHookList) {
                if (hook instanceof IMotionModifier mHook) {
                    motion = mHook.applyPerTick(AbstractModifiableProj.this, motion);
                }
            }
            return motion;
        }
        public void applyHitHooks(HitResult result) {
            if (motionHookList.isEmpty()) {
                return;
            }
            for (IHookModifier hook : motionHookList) {
                if (hook instanceof IOnHitModifier hHook) {
                    hHook.onHit(AbstractModifiableProj.this, result);
                }
            }
        }


        /**
         * Make the deltaMovement actually take effects
         */
        public void modifiedMove(AbstractModifiableProj proj) {
            proj.move(MoverType.SELF, proj.getDeltaMovement());
        }
    }
}
