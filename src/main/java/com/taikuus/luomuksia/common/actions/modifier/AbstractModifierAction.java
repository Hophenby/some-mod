package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotState;
import com.taikuus.luomuksia.api.wand.WandContext;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractModifierAction extends AbstractWandAction {
    public AbstractModifierAction(ResourceLocation id, String name, String desc) {
        super(id, name, desc);
    }

    @Override
    public void action(WandContext context, ShotState stats) {
        stats.addModifier(this);
        context.drawActions(1);
    }
    public abstract void applyModifier(AbstractModifiableProj proj);
}
