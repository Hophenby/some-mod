package com.taikuus.luomuksia.setup.client;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.WandAttrProvider;
import com.taikuus.luomuksia.common.item.Wand;
import com.taikuus.luomuksia.setup.ItemsAndBlocksRegistry;
import com.taikuus.luomuksia.setup.WandActionRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.taikuus.luomuksia.Luomuksia.MODID;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB_GENERAL = CREATIVE_MODE_TABS.register("tab_general", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.luomuksia")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemsAndBlocksRegistry.WAND.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(Wand.createWand(1));
                output.accept(Wand.createWand(3));
                output.accept(Wand.createWand(6));
                output.accept(Wand.createWand(9));
                output.accept(Wand.createWand(12));
                output.accept(Wand.createWand(new WandAttrProvider.TieredAttrBuilder(12)
                        .determineSpecificValue(RegistryNames.WAND_MANA.get(), Integer.MAX_VALUE)
                        .determineSpecificValue(RegistryNames.WAND_MANA_REGEN.get(), Integer.MAX_VALUE)
                        .determineSpecificValue(RegistryNames.WAND_MAX_MANA.get(), Integer.MAX_VALUE)
                        .determineSpecificValue(RegistryNames.WAND_BASIC_DELAY_TICKS.get(), Integer.MIN_VALUE)
                        .determineSpecificValue(RegistryNames.WAND_BASIC_RELOAD_TICKS.get(), Integer.MIN_VALUE)
                        .determineSpecificValue(RegistryNames.WAND_MAX_SLOTS.get(), 26)
                ));
                output.accept(ItemsAndBlocksRegistry.WAND_EDITING_TABLE_ITEM.get());
                for (AbstractWandAction action : WandActionRegistry.getAllRegistries().values()) {
                    output.accept(action.getActionItem());
                }
            }).build());
}
