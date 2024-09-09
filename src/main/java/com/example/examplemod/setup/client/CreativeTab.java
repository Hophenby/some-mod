package com.example.examplemod.setup.client;

import com.example.examplemod.api.actions.AbstractWandAction;
import com.example.examplemod.setup.ItemsAndBlocksRegistry;
import com.example.examplemod.setup.WandActionRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.example.examplemod.ExampleMod.MODID;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemsAndBlocksRegistry.WAND.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemsAndBlocksRegistry.WAND.get());
                output.accept(ItemsAndBlocksRegistry.WAND_EDITING_TABLE_ITEM.get());
                for (AbstractWandAction action : WandActionRegistry.getAllRegistries().values()) {
                    output.accept(action.getActionItem());
                }
            }).build());
}
