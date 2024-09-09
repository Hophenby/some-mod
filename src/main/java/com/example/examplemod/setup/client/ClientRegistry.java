package com.example.examplemod.setup.client;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.gui.WandEditingGui;
import com.example.examplemod.setup.EntityRegistry;
import com.example.examplemod.setup.MenuRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD)
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
