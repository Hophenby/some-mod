package com.taikuus.luomuksia.api.entity.proj;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ProjBounceHelper {
    /**
     * The number of bounces left on entities. If this is negative, it means infinite bounces.
     * <p></p>
     * If generallyConsumesBounce is true, this will be ignored.
     */
    private int leftBouncesOnEntity;
    /**
     * The number of bounces left on blocks. If this is negative, it means infinite bounces.
     * <p></p>
     * If generallyConsumesBounce is true, this will be considered as the number of total bounces left.
     */
    private int leftBouncesOnBlock;
    /**
     * If true, the left block bounces will be shared with left entity bounces.
     */
    private final boolean generallyConsumesBounce;
    /**
     * If true, the projectile will bounce infinitely.
     */
    private final boolean infiniteBouncesOnEntity;
    private final boolean infiniteBouncesOnBlock;

    public ProjBounceHelper(int leftBouncesOnEntity, int leftBouncesOnBlock, boolean generallyConsumesBounce) {
        this.leftBouncesOnEntity = generallyConsumesBounce ? leftBouncesOnBlock : leftBouncesOnEntity;
        this.leftBouncesOnBlock = leftBouncesOnBlock;
        this.generallyConsumesBounce = generallyConsumesBounce;
        this.infiniteBouncesOnEntity = leftBouncesOnEntity < 0;
        this.infiniteBouncesOnBlock = leftBouncesOnBlock < 0;
    }
    public ProjBounceHelper(int leftBouncesOnEntity, int leftBouncesOnBlock) {
        this(leftBouncesOnEntity, leftBouncesOnBlock, false);
    }
    public ProjBounceHelper(int leftBouncesOnAll) {
        this(leftBouncesOnAll, leftBouncesOnAll, true);
    }

    /**
     * If the projectile can bounce on the hit result. This will check if the projectile can bounce on the hit result.
     * @param hit The hit result.
     * @return If the projectile can bounce on the hit result.
     */
    public boolean canBounceOn(HitResult hit){
        if (hit.getType() == HitResult.Type.ENTITY){
            return canBounceOnEntity();
        } else if (hit.getType() == HitResult.Type.BLOCK){
            return canBounceOnBlock();
        }
        return false;
    }
    public boolean canBounceOnEntity(){
        return leftBouncesOnEntity != 0 || infiniteBouncesOnEntity;
    }
    public boolean canBounceOnBlock(){
        return leftBouncesOnBlock != 0 || infiniteBouncesOnBlock;
    }
    /**
     * Consumes a bounce.
     * @param hit The hit result.
     * @return If the bounce is consumed.
     */
    public boolean consumesBounces(HitResult hit){
        if ((hit.getType() == HitResult.Type.ENTITY && infiniteBouncesOnEntity) || (hit.getType() == HitResult.Type.BLOCK && infiniteBouncesOnBlock)){
            return true;
        }
        if (generallyConsumesBounce){
            leftBouncesOnBlock--;
            leftBouncesOnEntity--;
            return true;
        }
        boolean flag = false;
        if (hit.getType() == HitResult.Type.ENTITY && leftBouncesOnEntity != 0){
            flag = true;
            leftBouncesOnEntity--;
        } else if (hit.getType() == HitResult.Type.BLOCK && leftBouncesOnBlock != 0){
            flag = true;
            leftBouncesOnBlock--;
        }
        return flag;
    }

    /**
     * actually do the logic of bouncing on an entity
     * @param result the hit result
     * @param proj the projectile
     * @param restitutionCoef the restitution coefficient
     */
    public void tryBounceOnEntity(@NotNull EntityHitResult result, AbstractModifiableProj proj, float restitutionCoef){
        //TODO add hurt and knockback
        if (proj.getDeltaMovement().lengthSqr() < 1e-4) return;
        if (consumesBounces(result)){
            Entity target = result.getEntity();
            Vec3 motion = proj.getDeltaMovement();

            Direction.Axis hitDirectionAxis = proj.getBoundingBox().expandTowards(new Vec3(motion.x, 0, 0).normalize().scale(motion.length())).intersects(target.getBoundingBox()) ?
                    Direction.Axis.X : proj.getBoundingBox().expandTowards(new Vec3(0, motion.y, 0).normalize().scale(motion.length())).intersects(target.getBoundingBox()) ?
                    Direction.Axis.Y : Direction.Axis.Z;
            Direction hitDirection = Direction.fromAxisAndDirection(hitDirectionAxis, motion.get(hitDirectionAxis) > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);

            reverseVelocity(proj, hitDirectionAxis, restitutionCoef);
            //proj.playSound(proj.getHitSound(), 1.0F, 1.2F / (proj.getRandom().nextFloat() * 0.2F + 0.9F)); //TODO

            target.hurt(proj.getDamageSource(), proj.damage);
            proj.modifiersHelper.applyBounceHooks(result, hitDirection);

        }
    }
    /**
     * actually do the logic of bouncing on a block
     * @param result the hit result
     * @param proj the projectile
     * @param restitutionCoef the restitution coefficient
     */
    public void tryBounceOnBlock(@NotNull BlockHitResult result, AbstractModifiableProj proj, float restitutionCoef){
        if (proj.getDeltaMovement().lengthSqr() < 1e-4) return;
        if (consumesBounces(result)){
            Direction hitDirection = result.getDirection();
            Direction.Axis hitDirectionAxis = hitDirection.getAxis();

            reverseVelocity(proj, hitDirectionAxis, restitutionCoef);
            //proj.playSound(proj.getHitSound(), 1.0F, 1.2F / (proj.getRandom().nextFloat() * 0.2F + 0.9F)); //TODO

            proj.modifiersHelper.applyBounceHooks(result, hitDirection);
        }
    }

    /**
     * actually do the logic of bouncing on a hit result
     * <p>will automatically convert the bounce to the entity or block type</p>
     */
    public void tryBounce(AbstractModifiableProj proj, HitResult result, float restitutionCoef){
        if (result.getType() == HitResult.Type.ENTITY){
            tryBounceOnEntity((EntityHitResult) result, proj, restitutionCoef);
        } else if (result.getType() == HitResult.Type.BLOCK){
            tryBounceOnBlock((BlockHitResult) result, proj, restitutionCoef);
        }
    }
    private void reverseVelocity(AbstractModifiableProj proj, Direction.Axis axis, float restitutionCoef){
        proj.setDeltaMovement(proj.getDeltaMovement().scale(restitutionCoef).multiply(
                axis == Direction.Axis.X ? new Vec3(-1, 1, 1) :
                axis == Direction.Axis.Y ? new Vec3(1, -1, 1) :
                new Vec3(1, 1, -1)
        ));
    }
    public void addBounces(int bounces){
        leftBouncesOnEntity += bounces;
        leftBouncesOnBlock += bounces;
    }
    public void addBounces(int entityBounces, int blockBounces){
        leftBouncesOnEntity += entityBounces;
        leftBouncesOnBlock += blockBounces;
    }
}
