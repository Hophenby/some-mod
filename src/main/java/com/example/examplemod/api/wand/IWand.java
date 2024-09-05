package com.example.examplemod.api.wand;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface IWand {
    void createShot(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn);
    void afterShot(WandContext context, @NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn);
}
