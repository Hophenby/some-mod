package com.taikuus.luomuksia.common.entity.fx;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.setup.EntityRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class FadeLightFxProj extends AbstractModifiableProj {
    public FadeLightFxProj(EntityType<? extends AbstractModifiableProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FadeLightFxProj(AbstractModifiableProj from) {
        super(EntityRegistry.FADE_LIGHT.get(), from.getOwner(), from.getX(), from.getY(), from.getZ(), from.level());
        this.setDynamicLightLevel(from.getDynamicLightLevel());
        this.maxExistingTicksLimit = 16;
        this.maxExistingTicks = 16;
        this.noPhysics = true;
        this.setInvisible(true);
        this.setInvulnerable(true);
    }
    @Override
    public void tick(){
        super.tick();
        this.setDynamicLightLevel(Mth.clamp(this.getDynamicLightLevel() - 1, 0, 15));
    }
}
