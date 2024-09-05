package com.example.examplemod.api.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NumericWandAttr {
    public static final Codec<NumericWandAttr> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(NumericWandAttr::getId),
            Codec.DOUBLE.fieldOf("value").forGetter(NumericWandAttr::getDouble)
    ).apply(instance, NumericWandAttr::new));
    public static final Codec<NumericWandAttr> INT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(NumericWandAttr::getId),
            Codec.INT.fieldOf("value").forGetter(NumericWandAttr::getInt)
    ).apply(instance, NumericWandAttr::new));
    public static final StreamCodec<FriendlyByteBuf,NumericWandAttr> STREAM = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, NumericWandAttr::getId,
            ByteBufCodecs.DOUBLE, NumericWandAttr::getDouble,
            NumericWandAttr::new
    );
    public static final StreamCodec<FriendlyByteBuf,NumericWandAttr> INT_STREAM = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, NumericWandAttr::getId,
            ByteBufCodecs.INT, NumericWandAttr::getInt,
            NumericWandAttr::new
    );
    private final ResourceLocation id;
    private double value;
    public NumericWandAttr(ResourceLocation id, double value) {
        this.id = id;
        this.value = value;
    }
    public NumericWandAttr(ResourceLocation id, int value) {
        this(id, (double) value);
    }
    public ResourceLocation getId() {
        return id;
    }
    public double getDouble() {
        return value;
    }
    public Integer getInt() {
        return (int) value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return "NumericWandAttr{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
