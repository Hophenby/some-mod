package com.taikuus.luomuksia.api.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class CodecableWandAttr {
    public static final Codec<CodecableWandAttr> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(CodecableWandAttr::getId),
            Codec.INT.fieldOf("value").forGetter(CodecableWandAttr::getValue)
    ).apply(instance, CodecableWandAttr::new));
    public static final StreamCodec<FriendlyByteBuf, CodecableWandAttr> STREAM = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, CodecableWandAttr::getId,
            ByteBufCodecs.INT, CodecableWandAttr::getValue,
            CodecableWandAttr::new
    );
    private final ResourceLocation id;
    private int value;
    public CodecableWandAttr(ResourceLocation id, int value) {
        this.id = id;
        this.value = value;
    }
    public ResourceLocation getId() {
        return id;
    }
    public Integer getValue() {
        return value;
    }
    public boolean getIntAsBoolean() {
        return value != 0;
    }

    public void setValue(int value) {
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

    public Component getTooltip() {
        return Component.translatable("wand.attr." + id.toLanguageKey(), value);
    }
}
