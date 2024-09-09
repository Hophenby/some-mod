package com.example.examplemod.common.item;

import com.example.examplemod.RegistryNames;
import com.example.examplemod.api.wand.IWand;
import com.example.examplemod.api.wand.WandContext;
import com.example.examplemod.api.wand.WandData;
import com.example.examplemod.setup.DataComponentRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Wand extends Item implements IWand {
    public Wand(Properties properties) {
        super(properties);
    }
    public Wand(){
        super(new Item.Properties().stacksTo(1));
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        createShot(worldIn, playerIn, handIn);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
    }
    public static WandData readData(ItemStack stack) {
        return stack.get(DataComponentRegistry.WAND_DATA);
    }
    public static WandData readOrInitData(ItemStack stack) {
        WandData data = readData(stack);
        if (data == null) {
            data = new WandData();
            writeData(stack, data);
        }
        return data;
    }
    public static void writeData(ItemStack stack, WandData data) {
        stack.set(DataComponentRegistry.WAND_DATA, data.copy());
    }
    public static void writeDataNoCopy(ItemStack stack, WandData data) {
        stack.set(DataComponentRegistry.WAND_DATA, data);
    }

    @Override
    public void createShot(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        // read the data from the wand
        WandData data = readOrInitData(playerIn.getItemInHand(handIn));
        //LOGGER.debug("Wand data: " + data);
        // check if the wand is ready to shoot
        if (data.getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).getDoubleAsInt() > 0 ||
                data.getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).getDoubleAsInt() > 0) {
            return;
        }
        // create a new context
        WandContext context = new WandContext(
                data.getDeck(),
                data.getHand(),
                data.getDiscard(),
                data.getAttr(RegistryNames.WAND_MANA.get()).getDoubleAsInt(),
                data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).getDoubleAsInt());
        // shoot the wand
        context.shoot(worldIn, playerIn, handIn, this);
    }
    /**
     * Called after the shot is done
     * This is where the data should be written back to the wand
     */
    @Override
    public void afterShot(WandContext context, @NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        // write the data back to the wand
        WandContext.Getters getters = context.getGetters();
        WandData data = readData(playerIn.getItemInHand(handIn)).copy();

        // decks
        data.setDeck(getters.getDeck());
        data.setHand(getters.getHand());
        data.setDiscard(getters.getDiscard());

        // mana uses, reload and delay ticks
        data.getAttr(RegistryNames.WAND_MANA.get()).setValue(Math.clamp(getters.getStoredMana(), 0, data.getAttr(RegistryNames.WAND_MAX_MANA.get()).getDoubleAsInt()));
        data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).setValue(getters.getReloadTicks());
        data.getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).setValue(getters.getDelayTicks());

        // if the wand finished a turn with reload mark set true, accumulated reload ticks start the reload
        if (getters.getStartReload()) {
            data.getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).setValue(data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).getDoubleAsInt());
            data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).setValue(data.getAttr(RegistryNames.WAND_BASIC_RELOAD_TICKS.get()).getDoubleAsInt());
        }

        writeData(playerIn.getItemInHand(handIn), data);
        //LOGGER.debug("Wand data after shot: " + readData(playerIn.getItemInHand(handIn)).toString());
    }
    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected){
        WandData data = readOrInitData(pStack);
        data.tick();
        writeDataNoCopy(pStack, data);
    }
}
