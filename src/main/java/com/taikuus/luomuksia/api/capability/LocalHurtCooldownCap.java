package com.taikuus.luomuksia.api.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;

import static com.taikuus.luomuksia.setup.CapAndAttachmentRegistry.LOCAL_HURT_ATTACHMENT;

public class LocalHurtCooldownCap implements ILocalHurtCooldownCap{
    private SerializableCooldownMap cooldowns;
    private LivingEntity entity;

    public LocalHurtCooldownCap(LivingEntity entity) {

        this.cooldowns = entity.getData(LOCAL_HURT_ATTACHMENT);
        this.entity = entity;
    }

    @Override
    public int getHurtCooldown(Entity directEntity) {
        // check if the damage type is registered
        if (!cooldowns.containsKey(directEntity)) return 0;
        return cooldowns.get(directEntity);
    }

    @Override
    public void setHurtCooldown(Entity directEntity, int cooldown) {
        cooldowns.put(directEntity, Math.max(cooldown, 0));
        entity.setData(LOCAL_HURT_ATTACHMENT, cooldowns);
    }

    @Override
    public boolean canHurt(Entity directEntity) {
        return getHurtCooldown(directEntity) <= 0;
    }
    @Override
    public void tickCooldowns(){
        cooldowns.replaceAll((type, cd) -> Math.max(cd - 1, 0));
        entity.setData(LOCAL_HURT_ATTACHMENT, cooldowns);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return cooldowns.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        cooldowns.deserializeNBT(provider, nbt);
        entity.setData(LOCAL_HURT_ATTACHMENT, cooldowns);
    }
    public static class SerializableCooldownMap extends HashMap<Entity, Integer> implements INBTSerializable<CompoundTag> {
        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag nbt = new CompoundTag();
            forEach((entity, cd) -> nbt.putInt(entity.getStringUUID(), cd));
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
            forEach((entity, cd) -> put(entity, nbt.getInt(entity.getStringUUID())));
        }
    }
}
