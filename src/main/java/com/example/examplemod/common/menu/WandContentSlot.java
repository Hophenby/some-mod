package com.example.examplemod.common.menu;

import com.example.examplemod.api.actions.WandActionItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WandContentSlot extends Slot {
    private boolean active = true;

    public WandContentSlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }
    public void setActive(boolean active){
        this.active = active;
    }

    @Override
    public boolean isActive(){
        return active;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
    @Override
    public boolean mayPlace(ItemStack pStack) {
        return pStack.getItem() instanceof WandActionItem;
    }
}
