package com.taikuus.luomuksia.common.entity.projectile;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.setup.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ProjectileBouncingBall extends AbstractModifiableProj {
    private boolean hitFlag = false;
    private Direction.Axis hitDirectionAxis;
    public ProjectileBouncingBall(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ProjectileBouncingBall(Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(EntityRegistry.PROJECTILE_BOUNCING_BALL.get(), pOwner, pX, pY, pZ, pLevel);
        maxExistingTicks = 20 * 4;
        damage = 0.3f;
    }

    @Override
    public void tick(){
        super.tick();
        if (level().isClientSide){
            for (int i = 0; i < 4; i++){
                double x = random.nextGaussian() * 0.02;
                double y = random.nextGaussian() * 0.02;
                double z = random.nextGaussian() * 0.02;
                level().addParticle(ParticleTypes.COMPOSTER, getX(), getY(), getZ(), x, y, z);
            }
        }
    }
    @Override
    public boolean canHurtEntity(Entity entity) {
        return super.canHurtEntity(entity) && this.getDeltaMovement().lengthSqr() > 0.001f;
    }
    @Override
    public void move(MoverType pType, Vec3 pPos) {
        Vec3 oldMove = this.getDeltaMovement();
        super.move(pType, pPos);
        Vec3 newMove = this.getDeltaMovement();
        if (newMove.lengthSqr()<1e-4) {
            return;
        }
        boolean flagX = oldMove.x * newMove.x <= 1e-4;
        boolean flagY = oldMove.y * newMove.y <= 1e-4;
        boolean flagZ = oldMove.z * newMove.z <= 1e-4;
        boolean flag = flagX || flagY || flagZ;
        this.setDeltaMovement(flagX ? -oldMove.x : newMove.x, flagY ? -oldMove.y : newMove.y, flagZ ? -oldMove.z : newMove.z);
        //this.setDeltaMovement(this.getDeltaMovement().scale(flag ? 0.8f : 1f));
        //damage += 0.3f;
    }
    @Override
    protected void onHitEntity(@NotNull EntityHitResult result){
        this.hitFlag = true;
        Entity target = result.getEntity();
        Vec3 motion = this.getDeltaMovement();
        this.hitDirectionAxis = this.getBoundingBox().expandTowards(new Vec3(motion.x, 0, 0).normalize().scale(motion.length())).intersects(target.getBoundingBox()) ?
                Direction.Axis.X : this.getBoundingBox().expandTowards(new Vec3(0, motion.y, 0).normalize().scale(motion.length())).intersects(target.getBoundingBox()) ?
                Direction.Axis.Y : Direction.Axis.Z;
        super.onHitEntity(result);
    }
    @Override
    protected void onHitBlock(@NotNull BlockHitResult result){
        this.hitFlag = true;
        this.hitDirectionAxis = result.getDirection().getAxis();
        super.onHitBlock(result);
    }
    @Override
    protected void attemptRemoval() {
        if (this.hitFlag) {
            RandomSource random = RandomSource.create(this.timer);
            this.hitFlag = false;
            if (this.hitDirectionAxis != null) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.7f).multiply(
                        this.hitDirectionAxis == Direction.Axis.X ? new Vec3(-1, 1, 1) :
                                this.hitDirectionAxis == Direction.Axis.Y ? new Vec3(1, -1, 1) :
                                        new Vec3(1, 1, -1)
                ));
            }
            if (this.getDeltaMovement().lengthSqr() > 1e-4) {
                damage += 0.3f;
                this.playSound(SoundEvents.SLIME_BLOCK_HIT, 1.0F, 1.2F / (random.nextFloat() * 0.2F + 0.9F));
            }
            return;
        }
        super.attemptRemoval();
    }

}
