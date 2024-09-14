package com.taikuus.luomuksia.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ILocalHurtCooldownCap extends INBTSerializable<CompoundTag> {
    int getHurtCooldown(Entity causingEntity);
    void setHurtCooldown(Entity causingEntity, int cooldown);
    boolean canHurt(Entity causingEntity);
    void tickCooldowns();
}
