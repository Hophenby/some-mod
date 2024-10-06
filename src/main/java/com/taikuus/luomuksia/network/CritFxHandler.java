package com.taikuus.luomuksia.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CritFxHandler {
    public static void handleData(final CritFxPacket packet, final IPayloadContext context) {
        // Particles should be played in the main thread
        context.enqueueWork(() -> {
            Level clientWorld = Minecraft.getInstance().level;
            if (clientWorld == null) {
                return;
            }
            int entityID = packet.entityID();
            Entity entity = clientWorld.getEntity(entityID);
            RandomSource random = clientWorld.random;
            if (entity != null) {
                for (int i = 0; i < 10; i++) {
                    clientWorld.addParticle(ParticleTypes.CRIT,
                            entity.getX(), entity.getY() + entity.getBbHeight() / 2.0D, entity.getZ(),
                            random.nextGaussian(), 0.1D, random.nextGaussian());
                }
            }
        });

    }
}
