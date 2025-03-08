package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;

public interface ITrailerModifier extends IModifier{
    default void onTrailingAir(AbstractModifiableProj proj){
    }
    default void onTrailingAboveBlock(AbstractModifiableProj proj){
    }
    default void onTrailingAboveEntity(AbstractModifiableProj proj){
    }
}
