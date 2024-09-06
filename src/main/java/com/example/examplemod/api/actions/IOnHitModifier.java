package com.example.examplemod.api.actions;

import com.example.examplemod.api.entity.AbstractModifiableProj;
import net.minecraft.world.phys.HitResult;

public interface IOnHitModifier extends IHookModifier{
    default void onHit(AbstractModifiableProj proj, HitResult result){

    }
}
