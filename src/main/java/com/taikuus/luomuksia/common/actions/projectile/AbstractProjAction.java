package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.IModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.api.actions.EnumActionTypes;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractProjAction<P extends AbstractModifiableProj> extends AbstractWandAction implements IModifier {
    public AbstractProjAction(ResourceLocation id) {
        super(id, EnumActionTypes.PROJECTILE);
    }
    public abstract P relatedProjectile(WandContext context, ShotStates stats);
    public void action(WandContext context, ShotStates stats) {
        addDelayAndReload(context);
        stats.addProj(() -> {
            var proj = relatedProjectile(context, stats);
            proj.setInitMotion(stats.getPlayer().getLookAngle(),
                    AbstractProjAction.this.getNumericShowable(TooltipShowableStats.PROJECTILE_SPEED).floatValue() / 20F); // block per sec -> block per tick
            return proj;
        });
    }
    @Override
    public void applyModifier(AbstractModifiableProj proj) {
        proj.addInaccuracy(getInaccuracy());
    }
}
