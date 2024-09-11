package com.taikuus.luomuksia.common.actions.other;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;

public class TestAction extends AbstractWandAction {
    public static final TestAction INSTANCE = new TestAction();

    public TestAction() {
        super(
                RegistryNames.ACTION_TEST.get(),
                "Test Action",
                "This is a test action",
                EnumActionTypes.OTHER
        );
    }
    @Override
    public int getManaCost() {
        return 10;
    }


    @Override
    public void action(WandContext context, ShotStates stats) {
        Luomuksia.LOGGER.info("Test Action");
    }
}
