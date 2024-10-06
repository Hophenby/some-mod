package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;

/**
 * Interface for actions that modify the projectile. This is used for actions that modify the projectile in some way.
 */
public interface IModifier {
    void applyModifier(AbstractModifiableProj proj);
}
