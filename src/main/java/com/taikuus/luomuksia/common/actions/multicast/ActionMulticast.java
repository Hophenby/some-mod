package com.taikuus.luomuksia.common.actions.multicast;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;

public class ActionMulticast extends AbstractWandAction {
    public static final ActionMulticast INSTANCEx2 = new ActionMulticast(2, 0);
    public static final ActionMulticast INSTANCEx3 = new ActionMulticast(3, 0);
    public static final ActionMulticast INSTANCEx4 = new ActionMulticast(4, 10);
    public static final ActionMulticast INSTANCEx8 = new ActionMulticast(8, 30);
    private final int nx;
    private final int manaCostxn;
    public ActionMulticast(int nx, int manaCost) {
        super(RegistryNames.getRL("action_multicast_" + nx), "multicast_" + nx, "cast " + nx + " spell actions at once", EnumActionTypes.MULTICAST);
        this.nx = nx;
        this.manaCostxn = manaCost;
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        context.drawActions(nx);
    }
    @Override
    public int getManaCost() {
        return manaCostxn;
    }
}
