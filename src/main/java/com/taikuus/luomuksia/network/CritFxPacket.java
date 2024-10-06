package com.taikuus.luomuksia.network;

import com.taikuus.luomuksia.RegistryNames;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Receiving this packet means that the client should play the critical hit particles for the entity with the given ID.
 * @param entityID
 */
public record CritFxPacket(int entityID) implements CustomPacketPayload  {
    public static final CustomPacketPayload.Type<CritFxPacket> TYPE = new CustomPacketPayload.Type<>(RegistryNames.getRL("crit_fx"));
    public static final StreamCodec<FriendlyByteBuf, CritFxPacket> STREAM = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CritFxPacket::entityID,
            CritFxPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
