package com.taikuus.luomuksia.common.actions.modifier;


import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;

public class ModifierLight extends AbstractModifierAction {
    public static final ModifierLight INSTANCE = new ModifierLight();
    @Override
    public void applyModifier(AbstractModifiableProj proj) {
        proj.setDynamicLightLevel(proj.getDynamicLightLevel() + 15);
    }

    public ModifierLight() {
        super(RegistryNames.ACTION_MODIFIER_LIGHT.get());
        setNumericShowable(TooltipShowableStats.MANA_COST, 1);
    }
}
