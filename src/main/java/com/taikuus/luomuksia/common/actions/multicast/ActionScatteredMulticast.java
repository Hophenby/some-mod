package com.taikuus.luomuksia.common.actions.multicast;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.EnumActionTypes;
import com.taikuus.luomuksia.api.utils.ShapingFunctions;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class ActionScatteredMulticast extends AbstractWandAction {

    public static final ActionScatteredMulticast INSTANCEx2 = new ActionScatteredMulticast(2);
    public static final ActionScatteredMulticast INSTANCEx3 = new ActionScatteredMulticast(3);
    public static final ActionScatteredMulticast INSTANCEx4 = new ActionScatteredMulticast(4);
    private final Map<Integer, Double> NX_SCATTER = Map.of(
            2, Math.PI / 4,
            3, Math.PI / 6,
            4, Math.PI / 8
    );
    private final int nx;
    public ActionScatteredMulticast(int nx){
        super(RegistryNames.getRL("action_scattered_multicast_" + nx), EnumActionTypes.MULTICAST);
        this.nx = nx;
        setNumericShowable(TooltipShowableStats.MANA_COST, 20);
        setNumericShowable(TooltipShowableStats.SCATTER_DEGREE, Math.toDegrees(NX_SCATTER.get(nx)));
    }

    @Override
    public void play(WandContext context, ShotStates stats) {
        super.play(context, stats);
        stats.setShapingFunction(ShapingFunctions.fixedIntervalAngleH(NX_SCATTER.get(nx)));
        context.drawActions(nx);
    }
    @Override
    public Component getDescription() {
        return Component.translatable("tooltip.action.luomuksia.action_scattered.desc", nx);
    }

}
