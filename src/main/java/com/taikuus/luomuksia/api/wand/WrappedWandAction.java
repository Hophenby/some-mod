package com.taikuus.luomuksia.api.wand;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WrappedWandAction(AbstractWandAction action, int order){
    public static final Codec<WrappedWandAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbstractWandAction.CODEC.fieldOf("action").forGetter(WrappedWandAction::action),
            Codec.INT.fieldOf("order").forGetter(WrappedWandAction::order)
    ).apply(instance, WrappedWandAction::new));
    public static final StreamCodec<FriendlyByteBuf, WrappedWandAction> STREAM = StreamCodec.of(
            (buf, action) -> {
                AbstractWandAction.STREAM.encode(buf, action.action());
                buf.writeInt(action.order());
            },
            (buf) -> {
                AbstractWandAction action = AbstractWandAction.STREAM.decode(buf);
                int order = buf.readInt();
                return new WrappedWandAction(action, order);
            }
    );
}
