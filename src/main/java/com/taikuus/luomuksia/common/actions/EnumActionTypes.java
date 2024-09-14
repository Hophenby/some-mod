package com.taikuus.luomuksia.common.actions;

import com.taikuus.luomuksia.Luomuksia;
import net.minecraft.network.chat.Component;

public enum EnumActionTypes {
    PROJECTILE,
    MODIFIER,
    MULTICAST,
    OTHER,;
    public String get() {
        return this.name().toLowerCase();
    }
    public Component translatable() {
        return Component.translatable("tooltip.action_type." + Luomuksia.MODID + "." + get());
    }
}
