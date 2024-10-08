package com.taikuus.luomuksia.common.entity.projectile;

import com.taikuus.luomuksia.api.actions.IOnBounceModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.api.entity.proj.ProjBounceHelper;
import com.taikuus.luomuksia.setup.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileBouncingBall extends AbstractModifiableProj {
    //private boolean hitFlag = false;
    private final ProjBounceHelper bounceHelper = new ProjBounceHelper(0, 300);
    private Direction.Axis hitDirectionAxis;
    public ProjectileBouncingBall(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ProjectileBouncingBall(Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(EntityRegistry.PROJECTILE_BOUNCING_BALL.get(), pOwner, pX, pY, pZ, pLevel);
        maxExistingTicks = 20 * 4;
        damage += 0.3f;
        critFactor += 0.12f;
        this.modifiersHelper.addHook(new IOnBounceModifier() {
            @Override
            public void applyModifier(AbstractModifiableProj proj) {
                proj.getBouncer().addBounces(0, 200);

            }
            @Override
            public void onBounce(AbstractModifiableProj proj, HitResult result, Direction projFacing) {
                proj.damage += 0.03f;
            }
        });
    }
    @Override
    public ProjBounceHelper getBouncer() {
        return bounceHelper;
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
    public void move(MoverType pType, Vec3 pPos) {
        //Vec3 oldMove = this.getDeltaMovement();
        super.move(pType, pPos);
    }


}
