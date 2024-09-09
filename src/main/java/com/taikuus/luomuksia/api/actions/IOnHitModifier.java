package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import net.minecraft.world.phys.HitResult;

public interface IOnHitModifier extends IHookModifier{
    default void onHit(AbstractModifiableProj proj, HitResult result){

    }
}
