package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import com.hollingsworth.arsnouveau.client.particle.ColoredDynamicTypeData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TransferFXHandler {
    public static void handleData(final TransferFXPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level clientWorld = Minecraft.getInstance().level;
            if (clientWorld != null) {
                Vec3 fromPos = new Vec3(packet.fromPos().x(), packet.fromPos().y(), packet.fromPos().z());
                Vec3 toPos = new Vec3(packet.toPos().x(), packet.toPos().y(), packet.toPos().z());
                Vec3 facing = toPos.subtract(fromPos).normalize();
                Vec3 speed = facing.scale(0.1f);
                ExampleMod.LOGGER.debug("Adding particle at {} with speed {}", fromPos, speed);
                for (int i = 0; i < 10; i++) {
                    clientWorld.addParticle(getParticle(packet.color(), 1.5f, packet.relayTicks()),
                            fromPos.x() + clientWorld.random.nextDouble() * 0.4f - 0.2,
                            fromPos.y() + clientWorld.random.nextDouble() * 0.4f - 0.2 + 0.5,
                            fromPos.z() + clientWorld.random.nextDouble() * 0.4f - 0.2,
                            toPos.x(),
                            toPos.y(),
                            toPos.z());
                }
            }
        });
    }
    private static ParticleOptions getParticle(int color, float scale, int relayTicks) {
        return new ColoredDynamicTypeData(
                ModParticles.LINE_TYPE.get(),
                ParticleColor.fromInt(color),
                scale,
                relayTicks);
    }
}
