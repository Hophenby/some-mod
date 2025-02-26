package com.taikuus.luomuksia.common.item;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.*;
import com.taikuus.luomuksia.client.tooltip.WandTooltip;
import com.taikuus.luomuksia.network.LastCalcedActionsHandler;
import com.taikuus.luomuksia.setup.DataComponentRegistry;
import com.taikuus.luomuksia.setup.ItemsAndBlocksRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

public class Wand extends Item implements IWand {
    public Wand(){
        super(new Item.Properties().stacksTo(1));
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        InteractionResult result = createShot(worldIn, playerIn, handIn) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        return new InteractionResultHolder<>(result, playerIn.getItemInHand(handIn));
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
    public static ItemStack createWand(int tier){
        ItemStack stack = new ItemStack(ItemsAndBlocksRegistry.WAND.get());
        WandData data = WandData.fromTier(tier);
        writeData(stack, data);
        return stack;
    }
    public static ItemStack createWand(WandAttrProvider.TieredAttrBuilder builder){
        ItemStack stack = new ItemStack(ItemsAndBlocksRegistry.WAND.get());
        WandData data = WandData.custom(builder);
        writeData(stack, data);
        return stack;
    }

    @Override
    public boolean createShot(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        // read the wandData from the wand
        WandData data = readOrInitData(playerIn.getItemInHand(handIn));
        // Luomuksia.LOGGER.debug("Wand wandData: " + data);
        // check if the wand is ready to shoot
        if (data.getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).getValue() > 0 ||
                data.getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).getValue() > 0) {
            return false;
        }
        // if player is client side, just tell them they have successfully shot the wand
        if (worldIn.isClientSide) {
            return true;
        }
        // create a new context
        WandContext context = new WandContext(
                data.getDeck(),
                new ActionCardDeck(new ArrayList<>()),
                data.getDiscard(),
                data.getAttr(RegistryNames.WAND_MANA.get()).getValue(),
                data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).getValue());
        // shoot the wand
        context.shoot(worldIn, playerIn, handIn, this);
        return true;
    }
    /**
     * Called after the shot is done
     * This is where the wandData should be written back to the wand
     * This method only gets called on the server side as it should be checked before the shot
     */
    @Override
    public void afterShot(WandContext context, @NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        // write the wandData back to the wand
        WandContext.Getters getters = context.getGetters();
        WandData data = readData(playerIn.getItemInHand(handIn)).copy();

        // decks
        data.setDeck(getters.getDeck());
        data.setDiscard(getters.getDiscard());

        // mana uses, reload and delay ticks
        data.getAttr(RegistryNames.WAND_MANA.get()).setValue(Math.clamp(getters.getStoredMana(), 0, data.getAttr(RegistryNames.WAND_MAX_MANA.get()).getValue()));
        data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).setValue(getters.getReloadTicks());
        data.getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).setValue(getters.getDelayTicks());
        data.getAttr(RegistryNames.WAND_LAST_DELAY_TICKS.get()).setValue(getters.getDelayTicks());

        // if the wand finished a turn with reload mark set true, accumulated reload ticks start the reload
        if (getters.getStartReload()) {
            data.getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).setValue(data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).getValue());
            data.getAttr(RegistryNames.WAND_LAST_RELOAD_TICKS.get()).setValue(data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).getValue());
            data.getAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get()).setValue(data.getAttr(RegistryNames.WAND_BASIC_RELOAD_TICKS.get()).getValue());
        }
        // apply the cooldown
        // playerIn.getCooldowns().addCooldown(this, getters.getStartReload() ? Math.max(getters.getDelayTicks(), getters.getReloadTicks()) : getters.getDelayTicks());

        writeData(playerIn.getItemInHand(handIn), data);
        Luomuksia.LOGGER.debug("Wand wandData after shot: " + readData(playerIn.getItemInHand(handIn)).toString());

        if (context.isCastLoggable()){
            //send logged actions to the wand owner
            PacketDistributor.sendToPlayer((ServerPlayer) playerIn, new LastCalcedActionsHandler.LastCalcedActions(context.getLoggableCastActions()));
        }
    }
    //TODO apply cooldowns (not a vanilla one)
    public static void reloadWand(ItemStack wand) {
        if (wand.isEmpty() || !(wand.getItem() instanceof IWand)) {
            return;
        }
        WandData data = readOrInitData(wand).copy();

        ActionCardDeck deck = data.getDeck();
        deck.draw(data.getDiscard());
        deck.orderDeck();
        data.getDiscard().clear();

        writeData(wand, data);
    }
    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected){
        WandData data = readOrInitData(pStack);
        data.tick();
        writeDataNoCopy(pStack, data);
    }
    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        return Optional.of(new WandTooltip(readData(pStack)));
    }
}
