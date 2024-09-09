package com.example.examplemod.common.actions.modifier;

import com.example.examplemod.api.entity.AbstractModifiableProj;
import com.example.examplemod.api.actions.AbstractWandAction;
import com.example.examplemod.api.wand.ShotState;
import com.example.examplemod.api.wand.WandContext;
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
