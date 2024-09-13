package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.IModifierAction;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractModifierAction extends AbstractWandAction implements IModifierAction {
    public AbstractModifierAction(ResourceLocation id){
        super(id, EnumActionTypes.MODIFIER);
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        stats.addModifier(this);
        context.drawActions(1);
    }
}
