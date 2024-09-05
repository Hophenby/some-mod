package com.example.examplemod.event;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.network.TransferFXHandler;
import com.example.examplemod.network.TransferFXPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ExampleMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterEvent {
    @SubscribeEvent
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        // Register your packet handlers here
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(TransferFXPacket.TYPE, TransferFXPacket.STREAM_CODEC, TransferFXHandler::handleData);
    }
}
