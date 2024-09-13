package com.taikuus.luomuksia.setup.client;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.client.gui.WandEditingGui;
import com.taikuus.luomuksia.client.renderer.entity.ProjRendererStoneCutter;
import com.taikuus.luomuksia.client.tooltip.WandTooltip;
import com.taikuus.luomuksia.setup.EntityRegistry;
import com.taikuus.luomuksia.setup.MiscRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Luomuksia.MODID, bus = EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityRegistry.PROJECTILE_SPARK.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityRegistry.PROJECTILE_BOUNCING_BALL.get(), NoopRenderer::new);
        //TODO: Implement renderer maybe fireball renderer
        //TODO: add custom particles
        event.registerEntityRenderer(EntityRegistry.PROJECTILE_STONE_CUTTER.get(), ProjRendererStoneCutter::new);
    }
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event){
        event.register(MiscRegistry.WAND_EDITING_MENU.get(), WandEditingGui::new);
    }

    @SubscribeEvent
    public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(WandTooltip.class, WandTooltip.ClientWandTooltip::new);
        //TODO: add tooltips for actions
    }
}
