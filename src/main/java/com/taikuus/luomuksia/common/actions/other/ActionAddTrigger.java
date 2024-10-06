package com.taikuus.luomuksia.common.actions.other;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.EnumActionTypes;
import com.taikuus.luomuksia.api.actions.IModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.api.wand.WrappedWandAction;
import com.taikuus.luomuksia.common.actions.projectile.AbstractProjAction;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Supplier;

public class ActionAddTrigger extends AbstractWandAction {
    public static final ActionAddTrigger INSTANCE = new ActionAddTrigger();
    public ActionAddTrigger() {
        super(
                RegistryNames.ACTION_ADD_TRIGGER.get(),
                EnumActionTypes.OTHER
        );
        setNumericShowable(TooltipShowableStats.MANA_COST, 20);
    }
    @Override
    public void action(WandContext context, ShotStates stats) {
        /*
         * This action will draw 2 projectile actions from the deck, and add them as triggers to the wand.
         */
        AbstractProjAction<?> proj1 = null;
        AbstractProjAction<?> proj2 = null;

        // find 2 valid actions to add as triggers
        while (proj1 == null || proj2 == null) {
            // if the deck is empty, stop iterating
            if (context.getDeck().isEmpty()) {
                break;
            }

            // draw the action from the deck
            WrappedWandAction action = context.getDeck().remove(0);
            context.getDiscard().draw(action);

            // if the action is a projectile, add it to the triggers
            if (action.action() instanceof AbstractProjAction<?> projAction) {
                if (proj1 == null) {
                    proj1 = projAction;
                    context.spendMana(proj1.getManaCost());
                } else {
                    proj2 = projAction;
                    context.spendMana(proj2.getManaCost());
                }
            } else if(action.action() instanceof IModifier mod) {
                // if the action is a modifier, add it to the stats
                stats.addModifier(mod);
            }
        }
        // the case where both projectile actions are found
        if (proj2 != null) {
            // create a new state that will fire the second action
            // create a temporary state that will fire the first action
            ShotStates newStats = stats.childState();
            ShotStates tempStats = stats.childState();
            proj2.action(context, newStats);
            proj1.action(context, tempStats);
            List<Supplier<? extends Entity>> projList = tempStats.lastProjs();

            // add the first action as a trigger to the second action
            for (Supplier<? extends Entity> projSup : projList) {
                stats.addProj(()->{
                    Entity proj = projSup.get();
                    if ((proj instanceof AbstractModifiableProj modProj)) {
                        modProj.addHitTrigger(newStats);
                    }
                    return proj;
                });
            }
            /*
            AbstractProjAction finalProj = proj1;
            stats.addProj(() -> {

                AbstractModifiableProj proj = finalProj.relatedProjectile(context, stats);
                proj.addTrigger(newStats);
                return proj;
            });
             */
        } else if (proj1 != null) { // the case where only one projectile action is found
            proj1.action(context, stats);
        }
    }
}
