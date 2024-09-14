package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.common.menu.WandEditingMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
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
    // Local hurt cooldown for every registered DamageType
    public enum DamageTypeRegistry {
        CUT_DAMAGE(RegistryNames.DAMAGE_TYPE_CUT.get()),
        PROJ_DAMAGE(RegistryNames.DAMAGE_TYPE_PROJ.get()),
        ;
        private final ResourceLocation id;
        private final ResourceKey<DamageType> key;

        DamageTypeRegistry(ResourceLocation id) {
            this.id = id;
            this.key = ResourceKey.create(Registries.DAMAGE_TYPE, id);
        }

        public ResourceLocation getId() {
            return id;
        }

        public ResourceKey<DamageType> getKey() {
            return key;
        }
        public DamageType getDamageType(Level level){
            return level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(getKey()).value();
        }
        public DamageSource getDamageSource(Level level){
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(getKey()));
        }
        public DamageSource getDamageSource(Level level, Entity directSource){
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(getKey()), directSource);
        }
        public DamageSource getDamageSource(Level level, Entity directSource, Entity indirectSource){
            return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(getKey()), directSource, indirectSource);
        }
    }

}
