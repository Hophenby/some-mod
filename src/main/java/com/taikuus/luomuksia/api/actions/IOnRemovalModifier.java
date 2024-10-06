package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import net.minecraft.world.phys.HitResult;

public interface IOnRemovalModifier extends IModifier {
    default void onRemoval(AbstractModifiableProj proj, HitResult result){

    }
}
