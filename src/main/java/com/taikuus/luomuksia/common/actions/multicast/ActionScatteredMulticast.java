package com.taikuus.luomuksia.common.actions.multicast;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.IModifierAction;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;

import static com.taikuus.luomuksia.RegistryNames.ACTION_SCATTERED_MULTICAST;

public class ActionScatteredMulticast extends AbstractWandAction implements IModifierAction {

    public static final ActionScatteredMulticast INSTANCE = new ActionScatteredMulticast();
    public ActionScatteredMulticast() {
        super(ACTION_SCATTERED_MULTICAST.get(), "scattered multicast", "cast 2 spell actions at once and add a spread to the projectiles", EnumActionTypes.MULTICAST);
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        stats.addModifier(INSTANCE);
        context.drawActions(2);
    }

    @Override
    public void applyModifier(AbstractModifiableProj proj) {
        proj.inaccuracy += 2f;
    }
}
