package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import net.minecraft.world.phys.Vec3;

public interface IMotionModifier extends IHookModifier{
    default Vec3 applyPerTick(AbstractModifiableProj proj, Vec3 motion){
        return motion;
    }
}
