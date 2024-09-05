package com.example.examplemod.setup;

import com.example.examplemod.common.item.Wand;
import com.hollingsworth.arsnouveau.setup.registry.ItemRegistryWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.example.examplemod.ExampleMod.MODID;

public class ItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final ItemRegistryWrapper<Wand> WAND = new ItemRegistryWrapper<>(ITEMS.register("wand", ()-> new Wand()));
}
