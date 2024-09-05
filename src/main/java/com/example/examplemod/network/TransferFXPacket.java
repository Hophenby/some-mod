package com.example.examplemod.network;

import com.example.examplemod.ExampleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public record TransferFXPacket(Vector3f fromPos, Vector3f toPos, int relayTicks, int color) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TransferFXPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "transfer_fx"));
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<ByteBuf, TransferFXPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            TransferFXPacket::fromPos,
            ByteBufCodecs.VECTOR3F,
            TransferFXPacket::toPos,
            ByteBufCodecs.VAR_INT,
            TransferFXPacket::relayTicks,
            ByteBufCodecs.VAR_INT,
            TransferFXPacket::color,
            TransferFXPacket::new
    );


    @Override
    public ClientboundCustomPayloadPacket toVanillaClientbound() {
        return CustomPacketPayload.super.toVanillaClientbound();
    }

    @Override
    public ServerboundCustomPayloadPacket toVanillaServerbound() {
        return CustomPacketPayload.super.toVanillaServerbound();
    }
}
