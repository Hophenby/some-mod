package com.taikuus.luomuksia.api.wand.wandattr;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public record WandAttrInstance(Holder<WandAttr> attr, double value){
    public Mutable toMutable() {
        return new Mutable(attr, value);
    }
    public WandAttr getAttr() {
        return attr.value();
    }
    public static Codec<WandAttrInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WandAttr.CODEC.fieldOf("attr").forGetter(WandAttrInstance::attr),
            Codec.DOUBLE.fieldOf("value").forGetter(WandAttrInstance::value)
    ).apply(instance, WandAttrInstance::new));
    public static Codec<WandAttrInstance.Mutable> MUTABLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WandAttr.CODEC.fieldOf("attr").forGetter(WandAttrInstance.Mutable::getAttr),
            Codec.DOUBLE.fieldOf("value").forGetter(WandAttrInstance.Mutable::getValue)
    ).apply(instance, WandAttrInstance.Mutable::new));

    public static StreamCodec<RegistryFriendlyByteBuf, WandAttrInstance> STREAM = StreamCodec.of(
            (buf, data) -> {
                WandAttr.STREAM.encode(buf, data.attr());
                buf.writeDouble(data.value());
            },
            buf -> {
                Holder<WandAttr> attr = WandAttr.STREAM.decode(buf);
                double value = buf.readDouble();
                return new WandAttrInstance(attr, value);
            }
    );
    public static StreamCodec<RegistryFriendlyByteBuf, WandAttrInstance.Mutable> MUTABLE_STREAM = StreamCodec.of(
            (buf, data) -> {
                WandAttr.STREAM.encode(buf, data.getAttr());
                buf.writeDouble(data.getValue());
            },
            buf -> {
                Holder<WandAttr> attr = WandAttr.STREAM.decode(buf);
                double value = buf.readDouble();
                return new Mutable(attr, value);
            }
    );

    public Component getTooltip() {
        return Component.translatable("tooltip.wand_attr." + attr.value().getId().toLanguageKey(), (int) (value));
    }


    public static class Mutable {
        private final Holder<WandAttr> attr;
        private double value;

        public Mutable(Holder<WandAttr> attr, double value) {
            this.attr = attr;
            this.value = value;
        }

        public WandAttrInstance toImmutable() {
            return new WandAttrInstance(attr, value);
        }

        public Holder<WandAttr>  getAttr() {
            return attr;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
        public WandAttrInstance.Mutable copy() {
            return new WandAttrInstance.Mutable(attr, value);
        }

    }

}
