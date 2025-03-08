package com.taikuus.luomuksia.api.wand.wandattr;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RangedWandAttr extends WandAttr{
    private final double maxValue;
    private final double minValue;
    private final boolean reversedGrowth;
    public RangedWandAttr(ResourceLocation id, double defaultValue, double minValue, double maxValue, ResourceLocation iconTexture, double tooltipPriority) {
        super(id, defaultValue, defaultValue, iconTexture, tooltipPriority);
        this.reversedGrowth = minValue > maxValue;
        double min = Math.min(minValue, maxValue);
        double max = Math.max(minValue, maxValue);
        this.minValue = min;
        this.maxValue = max;
        if (defaultValue < min) {
            throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
        } else if (defaultValue > max) {
            throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
        }
    }
    @Override
    public double sanitizeValue(double value) {
        return Double.isNaN(value) ? this.minValue : Mth.clamp(value, this.minValue, this.maxValue);
    }
    public double getLinearIntpValue(float delta) {
        return reversedGrowth ? Mth.lerp(delta, this.maxValue, this.minValue) : Mth.lerp(delta, this.minValue, this.maxValue);
    }

}
