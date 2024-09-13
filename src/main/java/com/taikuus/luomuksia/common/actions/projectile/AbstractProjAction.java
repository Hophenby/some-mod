package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractProjAction extends AbstractWandAction {
    public AbstractProjAction(ResourceLocation id) {
        super(id, EnumActionTypes.PROJECTILE);
    }
    public abstract AbstractModifiableProj relatedProjectile(WandContext context, ShotStates stats);
    public void action(WandContext context, ShotStates stats) {
        stats.addProj(() -> relatedProjectile(context, stats));
    }
}
