package com.example.examplemod.setup;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.common.menu.WandEditingMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.MENU, ExampleMod.MODID);
    public static final Supplier<MenuType<WandEditingMenu>> WAND_EDITING_MENU = registerMenuType("wand_editing_menu", WandEditingMenu::new);

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return REGISTRAR.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
