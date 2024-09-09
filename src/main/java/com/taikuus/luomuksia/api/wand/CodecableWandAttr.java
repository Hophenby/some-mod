package com.taikuus.luomuksia.api.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class CodecableWandAttr {
    public static final Codec<CodecableWandAttr> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(CodecableWandAttr::getId),
            Codec.DOUBLE.fieldOf("value").forGetter(CodecableWandAttr::getDouble)
    ).apply(instance, CodecableWandAttr::new));
    public static final Codec<CodecableWandAttr> INT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(CodecableWandAttr::getId),
            Codec.INT.fieldOf("value").forGetter(CodecableWandAttr::getDoubleAsInt)
    ).apply(instance, CodecableWandAttr::new));
    public static final StreamCodec<FriendlyByteBuf, CodecableWandAttr> STREAM = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CodecableWandAttr::getId,
            ByteBufCodecs.DOUBLE, CodecableWandAttr::getDouble,
            CodecableWandAttr::new
    );
    public static final StreamCodec<FriendlyByteBuf, CodecableWandAttr> INT_STREAM = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CodecableWandAttr::getId,
            ByteBufCodecs.INT, CodecableWandAttr::getDoubleAsInt,
            CodecableWandAttr::new
    );
    private final ResourceLocation id;
    private double value;
    public CodecableWandAttr(ResourceLocation id, double value) {
        this.id = id;
        this.value = value;
    }
    public CodecableWandAttr(ResourceLocation id, int value) {
        this(id, (double) value);
    }
    public ResourceLocation getId() {
        return id;
    }
    public double getDouble() {
        return value;
    }
    public Integer getDoubleAsInt() {
        return (int) value;
    }
    public boolean getDoubleAsBoolean() {
        return value != 0;
    }

    public void setValue(double value) {
        this.value = value;
    }
    public void setValue(boolean value) {
        this.value = value ? 1 : 0;
    }
    @Override
    public String toString() {
        return "NumericWandAttr{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
