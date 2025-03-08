package com.taikuus.luomuksia.common.item;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.wand.ActionCardDeck;
import com.taikuus.luomuksia.api.wand.IWand;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.api.wand.WandData;
import com.taikuus.luomuksia.api.wand.wandattr.WandAttrProvider;
import com.taikuus.luomuksia.client.renderer.item.WandRenderer;
import com.taikuus.luomuksia.client.tooltip.WandTooltip;
import com.taikuus.luomuksia.network.LastCalcedActionsHandler;
import com.taikuus.luomuksia.setup.DataComponentRegistry;
import com.taikuus.luomuksia.setup.ItemsAndBlocksRegistry;
import com.taikuus.luomuksia.setup.WandAttrRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class Wand extends Item implements IWand, GeoItem {
    public Wand(){
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 1;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        playerIn.startUsingItem(handIn);
        return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (remainingUseDuration >= 0 && livingEntity instanceof Player player) {
            InteractionHand hand = player.getUsedItemHand();
            if (player.getItemInHand(hand) != stack) {
                return;
            }
            if (createShot(level, player, hand)) {
                player.swing(hand, true);
            }
        }
    }
    @Nullable
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
        if (data.getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).getValue() > 0 ||
                data.getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).getValue() > 0) {
            return false;
        }
        // if player is client side, just tell them they have successfully shot the wand
        if (worldIn.isClientSide) {
            return true;
        }
        // create a new context
        WandContext context = new WandContext(
                data.getDeck(),
                ActionCardDeck.empty(),
                data.getDiscard(),
                data.getMana(),
                (int) data.getMutable(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS).getValue());
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
        data.getMutable(WandAttrRegistry.ATTR_MANA).setValue(Math.clamp(getters.getStoredMana(), 0, data.getMaxMana()));
        data.getMutable(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS).setValue(getters.getReloadTicks());
        data.getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).setValue(getters.getDelayTicks());
        data.getMutable(WandAttrRegistry.ATTR_LAST_DELAY_TICKS).setValue(getters.getDelayTicks());

        // if the wand finished a turn with reload mark set true, accumulated reload ticks start the reload
        if (getters.getStartReload()) {
            data.getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).setValue(data.getMutable(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS).getValue());
            data.getMutable(WandAttrRegistry.ATTR_LAST_RELOAD_TICKS).setValue(data.getMutable(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS).getValue());
            data.getMutable(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS).setValue(data.getAttr(WandAttrRegistry.ATTR_BASIC_RELOAD_TICKS).value());
        }
        Luomuksia.LOGGER.info("Wand remaining reload ticks: " + data.getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).getValue()
                + " acc: " + data.getMutable(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS).getValue());
        // apply the cooldown
        // playerIn.getCooldowns().addCooldown(this, getters.getStartReload() ? Math.max(getters.getDelayTicks(), getters.getReloadTicks()) : getters.getDelayTicks());

        writeData(playerIn.getItemInHand(handIn), data);
        // manually sync the wand item to the player cause the usual sync logic doesn't work
        ((ServerPlayer) playerIn).connection.send(new ClientboundContainerSetSlotPacket(-2, 0,
                handIn == InteractionHand.MAIN_HAND ? playerIn.getInventory().selected : 40, playerIn.getItemInHand(handIn)));
        //Luomuksia.LOGGER.info("Wand wandData after shot: " + readData(playerIn.getItemInHand(handIn)).toString());

        if (context.isCastLoggable()){
            //send logged actions to the wand owner
            ((ServerPlayer) playerIn).connection.send(new LastCalcedActionsHandler.LastCalcedActions(context.getLoggableCastActions()));
        }
    }
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
        data.tickData();
        writeDataNoCopy(pStack, data);
    }
    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        return Optional.of(new WandTooltip(readData(pStack)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "controller",
                0,
                state -> state.setAndContinue(ANIMATION))
                );
    }
    private static final RawAnimation ANIMATION = RawAnimation.begin().thenLoop("animation.wand.idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
                new GeoRenderProvider(){
                    private final BlockEntityWithoutLevelRenderer renderer = new WandRenderer();
                    @Override
                    public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                        return renderer;
                    }
                }
        );
    }
}
