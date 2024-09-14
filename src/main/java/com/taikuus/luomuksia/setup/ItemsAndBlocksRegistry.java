package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.common.item.WandActionItem;
import com.taikuus.luomuksia.common.block.WandEditingTableBlock;
import com.taikuus.luomuksia.common.item.Wand;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import static com.taikuus.luomuksia.Luomuksia.MODID;

public class ItemsAndBlocksRegistry {
    public static final DeferredRegister<Item> ITEM_REGISTRAR = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final DeferredRegister<Block> BLOCK_REGISTRAR = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    public static final ItemRegistryWrapper<Wand> WAND = new ItemRegistryWrapper<>(ITEM_REGISTRAR.register("wand", ()-> new Wand()));

    public static final BlockRegistryWrapper<WandEditingTableBlock> WAND_EDITING_TABLE = new BlockRegistryWrapper<>(
            BLOCK_REGISTRAR.register("wand_editing_table", () -> new WandEditingTableBlock(Block.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE)))
    );
    public static final DeferredHolder<Item, BlockItem> WAND_EDITING_TABLE_ITEM = ITEM_REGISTRAR.register(
            "wand_editing_table",
            () -> new BlockItem(WAND_EDITING_TABLE.get(), new Item.Properties())
    );

    public static void onItemRegistry(RegisterEvent.RegisterHelper<Item> helper) {
        for (AbstractWandAction action : WandActionRegistry.getAllRegistries().values()) {
            WandActionItem actionItem = new WandActionItem(new Item.Properties(), action);
            helper.register(action.getId(), actionItem);
            action.actionItem = actionItem;
            Luomuksia.LOGGER.info("Registered action item: " + action.getId());
        }
    }

}
