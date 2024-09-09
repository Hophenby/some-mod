package com.example.examplemod.api.actions;

import net.minecraft.world.item.Item;

public class WandActionItem extends Item {
    private final AbstractWandAction action;
    public WandActionItem(Properties pProperties, AbstractWandAction action) {
        super(pProperties);
        this.action = action;
    }
    public AbstractWandAction getAction(){
        return action;
    }
}
