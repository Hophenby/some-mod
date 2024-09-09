package com.taikuus.luomuksia.common.actions;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotState;
import com.taikuus.luomuksia.api.wand.WandContext;

public class TestAction extends AbstractWandAction {
    public static final TestAction INSTANCE = new TestAction();

    public TestAction() {
        super(
                RegistryNames.ACTION_TEST.get(),
                "Test Action",
                "This is a test action"
        );
    }
    @Override
    public int getManaCost() {
        return 10;
    }


    @Override
    public void action(WandContext context, ShotState stats) {
        Luomuksia.LOGGER.info("Test Action");
    }
}
