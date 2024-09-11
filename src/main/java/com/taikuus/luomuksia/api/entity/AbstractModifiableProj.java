package com.taikuus.luomuksia.api.entity;

import com.taikuus.luomuksia.api.actions.IModifier;
import com.taikuus.luomuksia.api.actions.IModifierAction;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.actions.IOnHitModifier;
import com.taikuus.luomuksia.api.wand.ShotStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

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
    public boolean piercing = false;
    public float damage = 0.0f;
    public float knockback = 0.0f;
    public float gravity = 0.03f;
    public float fricCoef = 0.97f;
    public float inaccuracy = 0.0f;
    public float initVelocity = 1.0f;
    public Vec3 initAngle = Vec3.ZERO;
    public ShotStates triggeredShot;
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
    public boolean isExpired() {
        return timer > getMaxExistingTicks();
    }

    @Override
    public void tick() {
        timer++;
        if (!this.level().isClientSide && this.isExpired()) {
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
    protected void onHit(@NotNull HitResult result){
        result = transformHitResult(result);
        if (result == null) {
            return;
        }
        super.onHit(result);
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityResult) {
        super.onHitEntity(entityResult);
        if (!this.level().isClientSide) {
            if (this.getDamage() > 0){
                entityResult.getEntity().hurt(this.getDamageSource(), this.getDamage());
                if (this.getOwner() != null &&
                    this.getOwner() instanceof LivingEntity livingOwner) {
                    livingOwner.setLastHurtMob(entityResult.getEntity());
                }
            }
            if (this.knockback > 0) {
                entityResult.getEntity().push(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
            }
            if (!this.piercing) {
                attemptRemoval();
            }
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult pResult){
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            attemptRemoval();
        }
    }
    public DamageSource getDamageSource() {
        return new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(MOB_PROJECTILE), this, this.getOwner());
    }

    protected void attemptRemoval() {
        this.beforeRemoval();
        this.remove(RemovalReason.DISCARDED);
    }
    protected void beforeRemoval() {
        this.modifiersHelper.applyHitHooks(getHitResult());
        if (!this.level().isClientSide && this.triggeredShot != null) {
            this.triggeredShot.addModifier(proj -> proj.setPos(AbstractModifiableProj.this.position()));
            this.triggeredShot.applyModifiersAndShoot();
        }
    }
    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot() {
        super.shoot(initAngle.x, initAngle.y, initAngle.z, initVelocity, inaccuracy);
    }
    public void setInitMotion(Vec3 angle, float velocity, float inaccuracy) {
        this.initAngle = angle;
        this.initVelocity = velocity;
        this.inaccuracy = inaccuracy;
    }


    public void applyModifier(IModifierAction modifier) {
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

    public void addTrigger(ShotStates state) {
        this.triggeredShot = state;
    }

    /**
     * Helper class to apply tickable motion modifiers to the projectile
     */
    public class ModifiersHelper {
        private final List<IModifier> motionHookList = new ArrayList<>();
        public void addHook(IModifier hook) {
            motionHookList.add(hook);
        }
        public Vec3 applyMotiveHooks(Vec3 motion) {
            motion = motion.add(0, -gravity, 0).scale(fricCoef);
            if (onGround()){
                BlockPos groundPos = getBlockPosBelowThatAffectsMyMovement();
                motion = motion.scale(level().getBlockState(groundPos).getFriction(level(), groundPos, AbstractModifiableProj.this));
            }
            if (motionHookList.isEmpty()) {
                return motion;
            }
            for (IModifier hook : motionHookList) {
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
            for (IModifier hook : motionHookList) {
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
