package com.taikuus.luomuksia.common.actions.other;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.EnumActionTypes;
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
    public void play(WandContext context, ShotStates stats) {
        /*
         * This action will draw 2 projectile actions from the deck, and add them as triggers to the wand.
         */
        super.play(context, stats);

        AbstractProjAction<?> proj1 = null;

        // find a valid action to add as load
        // if the deck is empty, stop iterating
        while (proj1 == null && !context.getDeck().isEmpty()) {

            // draw the action from the deck
            WrappedWandAction action = context.getDeck().remove(0);
            context.getDiscard().draw(action);

            // if the action is a projectile, add it to the triggers
            if (action.action() instanceof AbstractProjAction<?> projAction) {
                proj1 = projAction;
                context.spendMana(proj1.getManaCost());
            }
        }
        // the case where the load action is found
        if (proj1 != null && !context.getDeck().isEmpty()) {
            // create a new state that will fire the following actions
            // create a temporary state that will fire the load
            ShotStates newStats = stats.childState(1);
            ShotStates tempStats = stats.childState();

            proj1.play(context, tempStats);
            context.parseTrigger(newStats);

            List<Supplier<? extends Entity>> projList = tempStats.lastProjs();

            // add the parsed actions as triggers to the load action
            for (Supplier<? extends Entity> projSup : projList) {
                stats.addProj(()->{
                    Entity proj = projSup.get();
                    if ((proj instanceof AbstractModifiableProj modProj)) {
                        modProj.addHitTrigger(newStats);
                    }
                    return proj;
                });
            }
        } else if (proj1 != null) { // the case where only one projectile action is found
            proj1.play(context, stats);
        }
    }
}
