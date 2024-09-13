package com.taikuus.luomuksia.common.menu;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.WandActionItem;
import com.taikuus.luomuksia.api.wand.ActionCardDeck;
import com.taikuus.luomuksia.api.wand.WandData;
import com.taikuus.luomuksia.api.wand.WrappedWandAction;
import com.taikuus.luomuksia.common.item.Wand;
import com.taikuus.luomuksia.setup.ItemsAndBlocksRegistry;
import com.taikuus.luomuksia.setup.MiscRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WandEditingMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Player player;
    private ItemStack lastWand;
    private int lastChangedTime = 0;
    private final WandContents wandContents;
    private Slot wandContentsSlot;
    private final Container wandSlotContainer = new SimpleContainer(1){
        @Override
        public void setChanged() {
            super.setChanged();
            WandEditingMenu.this.slotsChanged(this);
        }
    };
    public WandEditingMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }
    public WandEditingMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(MiscRegistry.WAND_EDITING_MENU.get(), pContainerId);
        this.access = pAccess;
        this.player = pPlayerInventory.player;
        this.wandContents = new WandContents(this, 0, 18);
        for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(pPlayerInventory, l, 8 + l * 18, 142 + 19));
        }
        for (int k = 0; k < 3; k++) {
            for (int i1 = 0; i1 < 9; i1++) {
                this.addSlot(new Slot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18 + 19));
            }
        }

        this.addSlot(new Slot(wandSlotContainer, 0, 8, 18){
            @Override
            public int getMaxStackSize() {
                return 1;
            }
            @Override
            public boolean mayPlace(@NotNull ItemStack pStack) {
                return pStack.is(ItemsAndBlocksRegistry.WAND.get());
            }
        });
        for (int i = 0; i < 27; i++) {
            this.addSlot(wandContents.getSlot(i));
        }
    }


    @Override
    public @NotNull ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack slotStackCopy = ItemStack.EMPTY;
        Luomuksia.LOGGER.debug("clicked slot: " + pIndex);
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStackClicked = slot.getItem(); // what slot do they click on
            slotStackCopy = slotStackClicked.copy();
            int wandSlotId = 36;
            if (pIndex == wandSlotId) { // if they click on the wand slot
                if (!this.moveItemStackTo(slotStackClicked, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (pIndex > wandSlotId) { // if they click on the wand contents
                if (!this.moveItemStackTo(slotStackClicked, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else { // if they click on the player inventory
                if (this.slots.get(wandSlotId).hasItem()) { // if the wand slot is not empty
                    for (int i = wandSlotId + 1; i < wandSlotId + 28; i++) { //check if itemstack may place in wand contents slots
                        if (
                                this.slots.get(i).isActive() &&
                                !this.slots.get(i).hasItem() &&
                                this.slots.get(i).mayPlace(slotStackClicked)
                        ) {
                            ItemStack slotStackCopy0 = slotStackClicked.copyWithCount(1);
                            slotStackClicked.shrink(1);
                            this.slots.get(i).setByPlayer(slotStackCopy0);
                        }
                    }
                } else { // if the wand slot is empty
                    if (!this.slots.get(wandSlotId).mayPlace(slotStackClicked)) {
                        return ItemStack.EMPTY;
                    }

                    ItemStack slotStackCopy1 = slotStackClicked.copyWithCount(1);
                    slotStackClicked.shrink(1);
                    this.slots.get(wandSlotId).setByPlayer(slotStackCopy1);
                }
            }

            if (slotStackClicked.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStackClicked.getCount() == slotStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, slotStackClicked);
        }

        return slotStackCopy;

    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(this.access, pPlayer, ItemsAndBlocksRegistry.WAND_EDITING_TABLE.get());
    }
    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39469_, p_39470_) -> this.clearContainer(pPlayer, this.wandSlotContainer));
    }
    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void slotsChanged(@NotNull Container pInventory) {
        if (pInventory == this.wandSlotContainer) {
            ItemStack mayBeWand = pInventory.getItem(0);
            lastChangedTime = this.player.tickCount;
            if (mayBeWand.getItem() instanceof Wand) {
                Wand.reloadWand(mayBeWand);
                this.lastWand = mayBeWand;
                int numSlot = Wand.readOrInitData(mayBeWand).getAttr(RegistryNames.WAND_MAX_SLOTS.get()).getValue();
                this.wandContents.deactivateAllSlots();
                for (int i = 0; i < numSlot; i++) {
                    WrappedWandAction wrappedAction = Wand.readOrInitData(mayBeWand).getAllActions().get(i);
                    ItemStack itemstack1 = wrappedAction == null ? ItemStack.EMPTY : wrappedAction.action().getActionItem().getDefaultInstance();
                    this.wandContents.getContainer(i).setItem(0, itemstack1);
                }
                this.wandContents.activateSlot(0, numSlot);
            }else {
                this.wandContents.deactivateAllSlots();
            }
        }
        if (this.wandContents.contains(pInventory)) {
            ItemStack wand = this.wandSlotContainer.getItem(0);
            if (!wand.isEmpty() && !player.level().isClientSide) {
                //Luomuksia.LOGGER.debug("Wand contents changed");
                WandData oldData = Wand.readOrInitData(wand);
                List<WrappedWandAction> list = new CopyOnWriteArrayList<>();
                for (int i: this.wandContents.getOpenedSlots()) {
                    ItemStack itemstack = this.wandContents.getContainer(i).getItem(0);
                    if (itemstack.isEmpty()) continue;
                    WrappedWandAction action = new WrappedWandAction(((WandActionItem) itemstack.getItem()).getAction(), i);
                    list.add(action);
                    //Luomuksia.LOGGER.debug("Added action to wand: " + action.action().getId() + " at index " + i);
                }
                ActionCardDeck deck = new ActionCardDeck(list);
                WandData newData = new WandData();
                newData.setDeck(deck);
                newData.setDiscard(oldData.getDiscard());
                newData.overwriteAllAttr(oldData.allAttr);
                //Luomuksia.LOGGER.debug("New wandData: " + newData);
                Wand.writeData(wand, newData);
            }
        }
        this.broadcastChanges();
        // reset wand contents slots in the menu

    }
}
