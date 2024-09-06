package com.example.examplemod.api.actions;

import com.example.examplemod.api.entity.AbstractModifiableProj;
import net.minecraft.world.phys.Vec3;

public interface IMotionModifier {
    default Vec3 applyPerTick(AbstractModifiableProj proj, Vec3 motion){
        return motion;
    }
}
