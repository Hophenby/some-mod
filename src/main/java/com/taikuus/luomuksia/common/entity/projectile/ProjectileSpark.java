package com.taikuus.luomuksia.common.entity.projectile;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.setup.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ProjectileSpark extends AbstractModifiableProj {

    public ProjectileSpark(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public ProjectileSpark(Entity pOwner, double pX, double pY, double pZ, Level pLevel) {
        super(EntityRegistry.PROJECTILE_SPARK.get(), pOwner, pX, pY, pZ, pLevel);
        maxExistingTicks += 20 * 4;
        damage += 1.0f;
        critFactor += 0.06f;
        setDynamicLightLevel(5);
    }

    @Override
    public void tick(){
        super.tick();
        if (level().isClientSide){
            for (int i = 0; i < 4; i++){
                double x = random.nextGaussian() * 0.02;
                double y = random.nextGaussian() * 0.02;
                double z = random.nextGaussian() * 0.02;
                level().addParticle(ParticleTypes.ELECTRIC_SPARK, getX(), getY(), getZ(), x, y, z);
            }
        }
    }

}
