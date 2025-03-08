package com.taikuus.luomuksia.api.wand.wandattr;

import com.mojang.serialization.Codec;
import com.taikuus.luomuksia.setup.WandAttrRegistry;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class WandAttr {

    public static final Codec<Holder<WandAttr>> CODEC = WandAttrRegistry.WAND_ATTR_REGISTRY.holderByNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WandAttr>> STREAM = ByteBufCodecs.holderRegistry(WandAttrRegistry.WAND_ATTR_REGISTRY_KEY);
    /**
     * The default value of the attribute.
     */
    private final double defaultValue;
    private final ResourceLocation id;
    private double baseValue;
    private final ResourceLocation iconTexture;
    private final boolean hidden;
    private final double tooltipPriority;
    public WandAttr(ResourceLocation id, double baseValue) {
        this(id, baseValue, baseValue);
    }
    private WandAttr(ResourceLocation id, double baseValue, double defaultValue, ResourceLocation iconTexture, boolean hidden, double tooltipPriority) {
        this.id = id;
        this.baseValue = baseValue;
        this.defaultValue = defaultValue;
        this.iconTexture = iconTexture;
        this.hidden = hidden;
        this.tooltipPriority = tooltipPriority;

    }
    private WandAttr(ResourceLocation id, double baseValue, double defaultValue, ResourceLocation iconTexture, boolean hidden) {
        this(id, baseValue, defaultValue, iconTexture, hidden, RandomSource.create().nextDouble());

    }
    public WandAttr(ResourceLocation id, double baseValue, double defaultValue, ResourceLocation iconTexture, double tooltipPriority) {
        this(id, baseValue, defaultValue, iconTexture, false, tooltipPriority);
    }

    public WandAttr(ResourceLocation id, double baseValue, double defaultValue) {
        this(id, baseValue, defaultValue, null, true);
    }

    public double getDefaultValue() {
        return defaultValue;
    }
    /**
     * Sanitizes the value of the attribute to fit within the expected parameter range of the attribute.
     * @return The sanitized attribute value.
     *
     * @param value The value of the attribute to sanitize.
     */
    public double sanitizeValue(double value) {
        return value;
    }

    public ResourceLocation getId() {
        return id;
    }
    public double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(int baseValue) {
        this.baseValue = baseValue;
    }
    @Override
    public String toString() {
        return "NumericWandAttr{" +
                "id=" + id +
                ", value=" + baseValue +
                '}';
    }

    public ResourceLocation getIconTexture() {
        return iconTexture;
    }

    public boolean isHidden() {
        return hidden;
    }

    public double getTooltipPriority() {
        return tooltipPriority;
    }
}
