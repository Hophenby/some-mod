package com.example.examplemod.common.block;

import com.example.examplemod.common.menu.WandEditingMenu;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class WandEditingTableBlock extends Block {

    public static final MapCodec<WandEditingTableBlock> CODEC = simpleCodec(WandEditingTableBlock::new);
    private static final Component CONTAINER_TITLE = Component.translatable("container.wand_editing_table");
    @Override
    @NotNull
    public MapCodec<? extends WandEditingTableBlock>codec(){
        return CODEC;
    }
    public WandEditingTableBlock(Properties properties) {
        super(properties);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
            return InteractionResult.CONSUME;
        }
    }
    @Override
    protected MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider(
                (pContainerId, pInventory, pPlayer) -> new WandEditingMenu(pContainerId, pInventory, ContainerLevelAccess.create(pLevel, pPos)),
                CONTAINER_TITLE
        );
    }

}
