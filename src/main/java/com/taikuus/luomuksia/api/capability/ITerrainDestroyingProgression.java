package com.taikuus.luomuksia.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface ITerrainDestroyingProgression extends INBTSerializable<CompoundTag> {
    float getRecordedBreakingAccumulation(BlockPos pos);

    void setRecordedBreakingAccumulation(BlockPos pos, float accumulation);
}
