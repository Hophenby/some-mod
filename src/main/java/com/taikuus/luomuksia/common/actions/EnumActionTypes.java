package com.taikuus.luomuksia.common.actions;

public enum EnumActionTypes {
    PROJECTILE,
    MODIFIER,
    MULTICAST,
    OTHER,;
    public String get() {
        return this.name().toLowerCase();
    }
}
