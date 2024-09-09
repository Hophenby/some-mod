package com.taikuus.luomuksia.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WandContents {
    private final Container[] wandContents = new Container[27];
    private final WandContentSlot[] wandContentsSlot = new WandContentSlot[27];
    private final WandEditingMenu boundMenu;
    public WandContents(WandEditingMenu menu, int xShift, int yShift) {
        this.boundMenu = menu;
        for (int i = 0; i < 27; i++) {
            wandContents[i] = new SimpleContainer(1);
            int finalI = i;
            wandContentsSlot[i] = new WandContentSlot(wandContents[finalI], 0, 8 + finalI % 9 * 18 + xShift, 18 + finalI / 9 * 18 + yShift){
                public boolean active = true;
                @Override
                public boolean isActive(){
                    return active;
                }
                public void setActive(boolean active){
                    this.active = active;
                    //ExampleMod.LOGGER.debug("Slot " + finalI + " set active: " + active);
                }
                @Override
                public void setChanged() {
                    super.setChanged();
                    //ExampleMod.LOGGER.debug("Slot changed");
                    if (!this.isActive()) return;
                    boundMenu.slotsChanged(wandContents[finalI]);
                }
            };
        }
    }
    public void deactivateAllSlots() {
        for (WandContentSlot slot : wandContentsSlot) {
            slot.setActive(false);
        }
    }
    public void activateSlot(int slot) {
        wandContentsSlot[slot].setActive(true);
    }
    public void activateSlot(int from, int to) {
        for (int i = from; i < to; i++) {
            activateSlot(i);
        }
    }
    public WandContentSlot getSlot(int slot) {
        return wandContentsSlot[slot];
    }
    public Container getContainer(int slot) {
        return wandContents[slot];
    }
    public boolean contains(Container container) {
        for (Container c : wandContents) {
            if (c == container) return true;
        }
        return false;
    }
    public List<Integer> getOpenedSlots() {
        List<Integer> openedSlots = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 27; i++) {
            if (wandContentsSlot[i].isActive()) {
                openedSlots.add(i);
            }
        }
        return openedSlots;
    }

}
