package com.example.examplemod.common.actions;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.RegistryNames;
import com.example.examplemod.api.wand.AbstractWandAction;
import com.example.examplemod.api.wand.ShotState;
import com.example.examplemod.api.wand.WandContext;

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
        ExampleMod.LOGGER.info("Test Action");
    }
}
