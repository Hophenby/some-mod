package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.HitResult;

public interface IOnBounceModifier extends IModifier{
    default void onBounce(AbstractModifiableProj proj, HitResult result, Direction projFacing){

    }
}
