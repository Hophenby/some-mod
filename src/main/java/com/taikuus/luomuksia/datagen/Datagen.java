package com.taikuus.luomuksia.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Datagen {
    public static CompletableFuture<HolderLookup.Provider> registries;
    public static PackOutput output;
    @SubscribeEvent
    public static void datagen(GatherDataEvent event) {
        registries = event.getLookupProvider();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        output = event.getGenerator().getPackOutput();
        new DamageTypesProvider(output, registries);
        event.getGenerator().addProvider(event.includeServer(), new DamageTypesProvider(output, registries));
        event.getGenerator().addProvider(event.includeServer(), new DamageTypesProvider.DamageTypesTagsProvider(output, registries, fileHelper));
    }
}
