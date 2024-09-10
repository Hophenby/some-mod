package com.taikuus.luomuksia.common.actions.multicast;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotState;
import com.taikuus.luomuksia.api.wand.WandContext;

public class ActionMultiCast extends AbstractWandAction {
    public static final ActionMultiCast INSTANCEx2 = new ActionMultiCast(2, 0);
    public static final ActionMultiCast INSTANCEx3 = new ActionMultiCast(3, 0);
    public static final ActionMultiCast INSTANCEx4 = new ActionMultiCast(4, 10);
    public static final ActionMultiCast INSTANCEx8 = new ActionMultiCast(8, 30);
    private final int nx;
    private final int manaCostxn;
    public ActionMultiCast(int nx, int manaCost) {
        super(RegistryNames.getRL("action_multicast_" + nx), "multicast_" + nx, "cast " + nx + " spell actions at once");
        this.nx = nx;
        this.manaCostxn = manaCost;
    }

    @Override
    public void action(WandContext context, ShotState stats) {
        context.drawActions(nx);
    }
    @Override
    public int getManaCost() {
        return manaCostxn;
    }
}
