package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.common.menu.WandEditingMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MiscRegistry {
    public static final DeferredRegister<MenuType<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.MENU, Luomuksia.MODID);
    public static final Supplier<MenuType<WandEditingMenu>> WAND_EDITING_MENU = registerMenuType("wand_editing_menu", WandEditingMenu::new);

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return REGISTRAR.register(name, () -> IMenuTypeExtension.create(factory));
    }
    public static final ResourceKey<DamageType> CUT_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, RegistryNames.DAMAGE_TYPE_CUT.get());

}
