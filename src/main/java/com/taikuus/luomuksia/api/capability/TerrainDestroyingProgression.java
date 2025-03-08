package com.taikuus.luomuksia.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;

import static com.taikuus.luomuksia.setup.CapAndAttachmentRegistry.TERRAIN_DESTROYING_ATTACHMENT;

public class TerrainDestroyingProgression implements ITerrainDestroyingProgression{
    private final SerializableProgressionMap progressionMap;
    private final Player player;
    private int breakID;

    public TerrainDestroyingProgression(Player player) {
        this.progressionMap = player.getData(TERRAIN_DESTROYING_ATTACHMENT);
        this.player = player;
        this.breakID = player.level().random.nextInt();
    }

    @Override
    public float getRecordedBreakingAccumulation(BlockPos pos) {
        return progressionMap.getOrDefault(pos, 0f);
    }

    public int getRecordedBreakingProgress(BlockPos pos, Level level) {
        return Mth.lerpInt((getRecordedBreakingAccumulation(pos) / getBlockExplosionResistance(pos, level)), 0, 9);
    }

    public SerializableProgressionMap getProgressionMap() {
        return progressionMap;
    }

    @Override
    public void setRecordedBreakingAccumulation(BlockPos pos, float accumulation) {
        progressionMap.put(pos, accumulation);
    }
    public void addBreakingAccumulation( BlockPos pos, float accumulation) {
        setRecordedBreakingAccumulation(pos, getRecordedBreakingAccumulation(pos) + accumulation);
    }
    public void tryAddAccumulationAndBreak(BlockPos pos, Level level, float accumulation) {
        if (level.getBlockState(pos).isAir()) return;
        addBreakingAccumulation(pos, accumulation);
        if (getRecordedBreakingAccumulation(pos) >= getBlockExplosionResistance(pos, level)) {
            level.destroyBlock(pos, true, player);
            progressionMap.remove(pos);
        }
    }
    public void tickProgression(){
        progressionMap.replaceAll((pos, acc) -> Math.max(acc - 0.01f, 0));
        progressionMap.entrySet().removeIf(entry -> entry.getValue() <= 0);
        player.setData(TERRAIN_DESTROYING_ATTACHMENT, progressionMap);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return progressionMap.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag nbt) {
        progressionMap.deserializeNBT(provider, nbt);
        player.setData(TERRAIN_DESTROYING_ATTACHMENT, progressionMap);
    }
    private float getBlockExplosionResistance(BlockPos pos, Level level) {
        return level.getBlockState(pos).getBlock().getExplosionResistance();
    }

    public int getBreakID() {
        return breakID;
    }

    public static class SerializableProgressionMap extends HashMap<BlockPos, Float> implements INBTSerializable<CompoundTag> {
        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("size", size());
            forEach((pos, acc) -> {
                CompoundTag entry = new CompoundTag();
                entry.putLong("pos", pos.asLong());
                entry.putFloat("acc", acc);
                nbt.put("entry" + size(), entry);
            });
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
            clear();
            int size = nbt.getInt("size");
            for (int i = 0; i < size; i++) {
                CompoundTag entry = nbt.getCompound("entry" + i);
                put(BlockPos.of(entry.getLong("pos")), entry.getFloat("acc"));
            }

        }
    }
}
