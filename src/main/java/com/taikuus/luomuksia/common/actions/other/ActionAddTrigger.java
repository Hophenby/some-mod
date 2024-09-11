package com.taikuus.luomuksia.common.actions.other;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.IModifierAction;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.api.wand.WrappedWandAction;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;

public class ActionAddTrigger extends AbstractWandAction {
    public static final ActionAddTrigger INSTANCE = new ActionAddTrigger();
    public ActionAddTrigger() {
        super(
                RegistryNames.ACTION_ADD_TRIGGER.get(),
                "Add Trigger",
                "Add a trigger to the wand",
                EnumActionTypes.OTHER
        );
    }
    @Override
    public int getManaCost() {
        return 20;
    }
    @Override
    public void action(WandContext context, ShotStates stats) {
        AbstractWandAction proj1 = null;
        AbstractWandAction proj2 = null;
        while (proj1 == null || proj2 == null) {
            if (context.getDeck().isEmpty()) {
                break;
            }
            WrappedWandAction action = context.getDeck().remove(0);
            context.getDiscard().draw(action);
            if (action.action().getType() == EnumActionTypes.PROJECTILE) {
                if (proj1 == null) {
                    proj1 = action.action();
                } else {
                    proj2 = action.action();
                }
            } else if(action.action() instanceof IModifierAction mod) {
                stats.addModifier(mod);
            }
        }
        if (proj2 != null) {
            ShotStates newStats = stats.childState();
            proj2.action(context, newStats);
            proj1.action(context, stats);
            if (stats.lastProj() instanceof AbstractModifiableProj proj) {
                proj.addTrigger(newStats);
            }
        } else if (proj1 != null) {
            proj1.action(context, stats);
        }
    }
}
