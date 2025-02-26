package com.taikuus.luomuksia.common.actions.multicast;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.actions.EnumActionTypes;
import com.taikuus.luomuksia.api.utils.ShapingFunctions;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import net.minecraft.network.chat.Component;

public class ActionShapedH extends AbstractWandAction{
    private final int nx;
    //create INSTANCEs of x2, x3, x4, x5, x6
    public static final ActionShapedH INSTANCEx2 = new ActionShapedH(2, 10);
    public static final ActionShapedH INSTANCEx3 = new ActionShapedH(3, 20);
    public static final ActionShapedH INSTANCEx5 = new ActionShapedH(5, 60);
    public static final ActionShapedH INSTANCEx6 = new ActionShapedH(6, 80);

    public ActionShapedH(int nx, int manaCost) {
        super(RegistryNames.getRL("action_shaped_h_" + nx), EnumActionTypes.MULTICAST);
        this.nx = nx;
        setNumericShowable(TooltipShowableStats.MANA_COST, manaCost);
        setNumericShowable(TooltipShowableStats.SCATTER_DEGREE, 360.0 / nx);
    }
    @Override
    public void play(WandContext context, ShotStates stats) {
        super.play(context, stats);
        stats.setShapingFunction(ShapingFunctions::normalPolygonH);
        for (int i = 0; i < nx; i++) {
            context.drawActions(1);
        }
    }
    @Override
    public Component getDescription() {
        if (nx == 2) return Component.translatable("tooltip.action.luomuksia.shaped_h_2.desc");
        return Component.translatable("tooltip.action.luomuksia.shaped_h.desc", nx);
    }
}
