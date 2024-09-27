package com.taikuus.luomuksia.common.actions.multicast;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.IModifier;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.api.actions.EnumActionTypes;

import java.util.Map;

import static com.taikuus.luomuksia.RegistryNames.ACTION_SCATTERED_MULTICAST;

public class ActionScatteredMulticast extends AbstractWandAction implements IModifier {

    public static final ActionScatteredMulticast INSTANCE = new ActionScatteredMulticast();
    public ActionScatteredMulticast() {
        super(ACTION_SCATTERED_MULTICAST.get(), EnumActionTypes.MULTICAST);
        setNumericShowable(TooltipShowableStats.MANA_COST, 20);
        setNumericShowable(TooltipShowableStats.PROJECTILE_INACCURACY, 15f);
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        stats.addModifier(INSTANCE);
        context.drawActions(2);
    }

    @Override
    public void applyModifier(AbstractModifiableProj proj) {
        proj.inaccuracy += 15f;
    }
    @Override
    public Map<TooltipShowableStats, String> getTooltipShowables() {
        Map<TooltipShowableStats, String> map = super.getTooltipShowables();
        map.put(TooltipShowableStats.PROJECTILE_INACCURACY, "15");
        return map;
    }
}
