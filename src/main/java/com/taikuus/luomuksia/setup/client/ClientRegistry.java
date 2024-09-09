package com.taikuus.luomuksia.setup.client;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.client.gui.WandEditingGui;
import com.taikuus.luomuksia.setup.EntityRegistry;
import com.taikuus.luomuksia.setup.MenuRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Luomuksia.MODID, bus = EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientRegistry {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(EntityRegistry.PROJECTILE_SPARK.get(), NoopRenderer::new);
    }
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event){
        event.register(MenuRegistry.WAND_EDITING_MENU.get(), WandEditingGui::new);
    }
}
