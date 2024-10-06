package com.taikuus.luomuksia.api.entity.proj;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.actions.*;
import com.taikuus.luomuksia.api.client.lighter.ProjLightHelper;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.client.lighter.ProjLightUtils;
import com.taikuus.luomuksia.common.entity.fx.FadeLightFxProj;
import com.taikuus.luomuksia.network.CritFxPacket;
import com.taikuus.luomuksia.setup.MiscRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public boolean hurtEntity = true;
    public boolean piercing = false;
    public float damage = 0.0f;
    public float knockback = 0.0f;
    public float gravity = 0.03f;
    public float fricCoef = 0.97f;
    public float inaccuracy = 0.0f;
    public float initVelocity = 1.0f;
    private final ProjLightHelper lighter = new ProjLightHelper(this);
    private final ProjBounceHelper bouncer = new ProjBounceHelper(0);
    protected int localHurtCooldown = 8;
    protected double critFactor = 0;
    public Vec3 initAngle = Vec3.ZERO;
    public ShotStates deathTrigger;
    public ShotStates hitTrigger;
    public static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIGHT_LEVEL = SynchedEntityData.defineId(AbstractModifiableProj.class, EntityDataSerializers.INT);
    public final ModifiersHelper modifiersHelper = new ModifiersHelper();
    protected AbstractModifiableProj(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    protected AbstractModifiableProj(EntityType<? extends AbstractModifiableProj> pEntityType, Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
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
        return getProjBoundDamage();
    }
    private boolean nextCritChance(EntityHitResult result) {
        var critFactor = getCalcedCritFactor(result);
        return random.nextFloat() < critFactor;
    }
    private float getCritConsideredDamage(EntityHitResult result, boolean critFlag){
        return critFlag ? getDamage() * (Math.max(0.75f, (float) getCalcedCritFactor(result)) + 1) : getDamage();
    }

    public float getProjBoundDamage() {
        return damage;
    }

    public ProjLightHelper getLighter() {
        return lighter;
    }

    /**
     * Define the wandData that should be synchronized between the client and server
     *
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(LIGHT_LEVEL, 0);
        pBuilder.define(OWNER_ID, -1);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(OWNER_ID, tag.getInt("owner_id"));
        this.entityData.set(LIGHT_LEVEL, tag.getInt("light_level"));
        //Luomuksia.LOGGER.debug("readAdditionalSaveData() " + this.entityData.get(LIGHT_LEVEL));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("owner_id", this.entityData.get(OWNER_ID));
        tag.putInt("light_level", this.entityData.get(LIGHT_LEVEL));
    }
    public boolean isExpired() {
        return timer > getMaxExistingTicks();
    }

    @Override
    public void tick() {
        // The projectile will be removed if it exists for too long.
        timer++;
        if (!this.level().isClientSide && this.isExpired()) {
            this.attemptRemoval();
            return;
        }

        // The projectile might hit something. We should help it find the target.
        Vec3 thisPosition = this.position();
        Vec3 nextPosition = getNextHitPosition();
        traceAnyHit(getHitResult(), thisPosition, nextPosition);

        // The projectile should move and do its tickable part.
        Vec3 motion = getDeltaMovement();
        motion = modifiersHelper.applyMotiveHooks(motion);
        setDeltaMovement(motion);
        this.move(MoverType.SELF, this.getDeltaMovement());

        super.tick();
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (pKey.equals(LIGHT_LEVEL) && this.level().isClientSide) {
            int dynamicLightLevel = this.entityData.get(LIGHT_LEVEL);
            if (dynamicLightLevel <= 0) {
                ProjLightUtils.removeLightSource(this);
            } else {
                ProjLightUtils.addLightSource(this);
            }
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result){
        result = transformHitResult(result);
        if (result == null) {
            return;
        }
        if (!this.level().isClientSide && this.hitTrigger != null) {
            this.hitTrigger.addModifier(proj -> proj.setPos(AbstractModifiableProj.this.position()));
            this.hitTrigger.applyModifiersAndShoot();
        }
        this.modifiersHelper.applyHitHooks(result);
        super.onHit(result);
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult entityResult) {
        super.onHitEntity(entityResult);
        if (!this.level().isClientSide) {
            if (this.getDamage() > 0 && this.canHurtEntity(entityResult.getEntity())){
                boolean critFlag = nextCritChance(entityResult);
                //Luomuksia.LOGGER.debug("Crit chance: " + getCalcedCritFactor(entityResult) + " critFlag: " + critFlag);
                if (critFlag) {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    PacketDistributor.sendToPlayersNear((ServerLevel) level(), null, getX(), getY(), getZ(),32, new CritFxPacket(this.getId()));
                    //Luomuksia.LOGGER.debug("Crit!");
                }
                entityResult.getEntity().hurt(this.getDamageSource(), getCritConsideredDamage(entityResult, critFlag));
                // entityResult.getEntity().hurt(this.getDamageSource(), this.getDamage());
                if (this.getOwner() != null &&
                    this.getOwner() instanceof LivingEntity livingOwner) {
                    livingOwner.setLastHurtMob(entityResult.getEntity());
                }
            }
            if (this.knockback > 0) {
                entityResult.getEntity().push(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
            }
            this.attemptRemoval();
        }
    }
    public boolean canHurtEntity(Entity entity) {
        return this.hurtEntity && (entity != this.getOwner() || this.piercing);
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult){
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            attemptRemoval();
        }
    }
    public DamageSource getDamageSource() {
        return MiscRegistry.DamageTypeRegistry.PROJ_DAMAGE.getDamageSource(this.level(),this, this.getOwner());
    }

    protected void attemptRemoval() {
        //this.beforeRemoval();
        if (!this.piercing) {
            this.attemptRemovalNoPiercingCheck();
        } else {
            this.beforeRemoval();
        }
    }
    protected void attemptRemovalNoPiercingCheck() {
        this.beforeRemoval();
        if (!(this instanceof FadeLightFxProj)){
            if (this.getDynamicLightLevel() > 0) {
                FadeLightFxProj fx = new FadeLightFxProj(this);
                this.level().addFreshEntity(fx);
            }
        }
        this.remove(RemovalReason.DISCARDED);
    }
    protected void beforeRemoval() {
        this.modifiersHelper.applyRemovalHooks(getHitResult());
        if (!this.level().isClientSide && this.deathTrigger != null) {
            this.deathTrigger.addModifier(proj -> proj.setPos(AbstractModifiableProj.this.position()));
            this.deathTrigger.applyModifiersAndShoot();
        }
    }
    @Override
    public void onClientRemoval() {
        if (this.getDynamicLightLevel() > 0) {
            ProjLightUtils.removeLightSource(this);
            ProjLightUtils.checkLightSources();
        }
    }
    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot() {
        super.shoot(initAngle.x, initAngle.y, initAngle.z, initVelocity, inaccuracy);
    }
    public void setInitMotion(Vec3 angle, float velocity) {
        this.initAngle = angle;
        this.initVelocity = velocity;
    }


    public void applyModifier(IModifier modifier) {
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
    public void traceAnyHit(@Nullable HitResult rayTraceResult, Vec3 thisPosition, Vec3 nextPosition) {
        if (rayTraceResult != null && rayTraceResult.getType() != HitResult.Type.MISS) {
            nextPosition = rayTraceResult.getLocation();
        }
        EntityHitResult entityRayTraceResult = this.findHitEntity(thisPosition, nextPosition);
        if (entityRayTraceResult != null) {
            rayTraceResult = entityRayTraceResult;
        }

        if (rayTraceResult != null && rayTraceResult.getType() != HitResult.Type.MISS && !net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, rayTraceResult)) {
            if (this.getBouncer().canBounceOn(rayTraceResult)) {
                this.getBouncer().tryBounce(this, rayTraceResult, 0.7f);
            } else {
                this.onHit(rayTraceResult);
                this.hasImpulse = true;
            }
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

    public void addDeathTrigger(ShotStates state) {
        this.deathTrigger = state;
    }
    public void addHitTrigger(ShotStates state) {
        this.hitTrigger = state;
    }

    public int getLocalHitCooldown() {
        return localHurtCooldown;
    }

    public int getDynamicLightLevel() {
        return this.lighter.getDynamicLightLevel();
    }

    public double getProjBoundCritFactor() {
        return critFactor;
    }

    public double getCalcedCritFactor(EntityHitResult result){
        return modifiersHelper.getCalcedCritFactor(result);
    }

    public void setDynamicLightLevel(int dynamicLightLevel) {
        this.lighter.setDynamicLightLevel(dynamicLightLevel);
    }

    public ProjBounceHelper getBouncer() {
        return bouncer;
    }

    public void addInaccuracy(double inaccuracy) {
        this.inaccuracy += inaccuracy;
    }

    /**
     * Helper class to apply tickable motion modifiers to the projectile
     */
    public class ModifiersHelper {
        private final List<IModifier> hookList = new ArrayList<>();
        public void addHook(IModifier hook) {
            hookList.add(hook);
        }
        public Vec3 applyMotiveHooks(Vec3 motion) {
            motion = motion.add(0, -gravity, 0).scale(fricCoef);
            if (onGround()){
                BlockPos groundPos = getBlockPosBelowThatAffectsMyMovement();
                motion = motion.scale(level().getBlockState(groundPos).getFriction(level(), groundPos, AbstractModifiableProj.this));
            }
            if (hookList.isEmpty()) {
                return motion;
            }
            for (IModifier hook : hookList) {
                if (hook instanceof IMotionModifier mHook) {
                    motion = mHook.applyMotivePerTick(AbstractModifiableProj.this, motion);
                }
            }
            return motion;
        }
        public void applyHitHooks(HitResult result) {
            if (hookList.isEmpty()) {
                return;
            }
            for (IModifier hook : hookList) {
                if (hook instanceof IOnHitModifier hHook) {
                    hHook.onHit(AbstractModifiableProj.this, result);
                }
            }
        }
        public void applyRemovalHooks(HitResult result) {
            if (hookList.isEmpty()) {
                return;
            }
            for (IModifier hook : hookList) {
                if (hook instanceof IOnRemovalModifier removeHook) {
                    removeHook.onRemoval(AbstractModifiableProj.this, result);
                }
            }
        }
        public void applyBounceHooks(HitResult result, Direction projFacing) {
            if (hookList.isEmpty()) {
                return;
            }
            for (IModifier hook : hookList) {
                if (hook instanceof IOnBounceModifier bounceHook) {
                    bounceHook.onBounce(AbstractModifiableProj.this, result, projFacing);
                }
            }
        }
        public double getCalcedCritFactor(EntityHitResult result){
            LinkedList<ICritModifier> adds = new LinkedList<>();
            LinkedList<ICritModifier> muls = new LinkedList<>();
            LinkedList<ICritModifier> indMuls = new LinkedList<>();
            LinkedList<ICritModifier> set = new LinkedList<>();
            for (IModifier hook : hookList) {
                if (hook instanceof ICritModifier critHook) {
                    switch (critHook.getCalcType()) {
                        case ADD -> adds.add(critHook);
                        case MUL -> muls.add(critHook);
                        case IND_MUL -> indMuls.add(critHook);
                        case SET -> set.add(critHook);
                    }
                }
            }
            // calc order:
            // temp var valueA = baseCrit
            // for each ADD hook, valueA applies the hook
            // if SET hooks exist, valueA is the last SET hook value, overriding all ADD hooks
            // the current result saves into valueB
            // then get a sum of valueB applied by each MUL hook
            // then save the sum into valueC and subtract valueA * (num of MUL hooks - 1) from valueC for compensation
            // then get a product of valueC applied by each IND_MUL hook
            // then return the product
            double valueA = AbstractModifiableProj.this.getProjBoundCritFactor();
            if (!set.isEmpty()) {
                valueA = set.getLast().modifyValue(valueA, result, AbstractModifiableProj.this);
            } else {
                for (ICritModifier hook : adds) {
                    valueA = hook.modifyValue(valueA, result, AbstractModifiableProj.this);
                }
            }
            double valueC = - (valueA * (muls.size() - 1)); // compensation
            for (ICritModifier hook : muls) {
                valueC += hook.modifyValue(valueA, result, AbstractModifiableProj.this); // valueB
            }
            for (ICritModifier hook : indMuls) {
                valueC = hook.modifyValue(valueC, result, AbstractModifiableProj.this); // valueC
            }
            return valueC;

        }

    }
}
