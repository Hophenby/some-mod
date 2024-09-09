package com.taikuus.luomuksia.event;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.network.TransferFXHandler;
import com.taikuus.luomuksia.network.TransferFXPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Luomuksia.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegisterEvent {
    @SubscribeEvent
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        // Register your packet handlers here
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(TransferFXPacket.TYPE, TransferFXPacket.STREAM_CODEC, TransferFXHandler::handleData);
    }
}
