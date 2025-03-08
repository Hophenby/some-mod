package com.taikuus.luomuksia.event;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.client.lighter.ProjLightUtils;
import com.taikuus.luomuksia.api.wand.IWand;
import com.taikuus.luomuksia.utils.IUseCooldownSetter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Used to handle client events.
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Luomuksia.MODID)
public class ClientEvent {

    /**
     * Used to catch the renderWorldLastEvent in order to draw the debug nodes for pathfinding.
     *
     * @param event the catched event.
     * @author baileyholl
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderWorldLastEvent(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            ProjLightUtils.updateAll(event.getLevelRenderer());
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player != null &&
                (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWand
                        || player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof IWand)) {
            // reset the vanilla cooldown
            ((IUseCooldownSetter) Minecraft.getInstance()).setItemUseCooldown(0);
        }
    }


}
