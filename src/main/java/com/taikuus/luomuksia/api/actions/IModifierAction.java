package com.taikuus.luomuksia.api.actions;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;

public interface IModifierAction {
    void applyModifier(AbstractModifiableProj proj);
}
