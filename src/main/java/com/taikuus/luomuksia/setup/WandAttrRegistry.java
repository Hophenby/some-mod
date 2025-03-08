package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.wandattr.RangedWandAttr;
import com.taikuus.luomuksia.api.wand.wandattr.WandAttr;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class WandAttrRegistry {
    private static int nextId = 0;
    public static final ResourceKey<Registry<WandAttr>> WAND_ATTR_REGISTRY_KEY = ResourceKey.createRegistryKey(RegistryNames.getRL("wand_attr"));
    public static final Registry<WandAttr> WAND_ATTR_REGISTRY = new RegistryBuilder<>(WAND_ATTR_REGISTRY_KEY)
            .sync(true)
            // Effectively limits the max count. Generally discouraged, but may make sense in settings such as networking.
            .maxId(256)
            // Build the registry.
            .create();

    public static final DeferredRegister<WandAttr> WAND_ATTR_REGISTRAR = DeferredRegister.create(WAND_ATTR_REGISTRY, Luomuksia.MODID);
    public static final Holder<WandAttr> ATTR_TIER = register(RegistryNames.WAND_TIER, 1, 1, 12, "textures/wand_attr/tier.png");
    public static final Holder<WandAttr> ATTR_MAX_MANA = register(RegistryNames.WAND_MAX_MANA, 50, 50, 2500, "textures/wand_attr/max_mana.png");
    public static final Holder<WandAttr> ATTR_MANA_REGEN = register(RegistryNames.WAND_MANA_REGEN, 1, 1, 100, "textures/wand_attr/mana_regen.png");
    public static final Holder<WandAttr> ATTR_BASIC_RELOAD_TICKS = register(RegistryNames.WAND_BASIC_RELOAD_TICKS, 0, 60, -60, "textures/wand_attr/basic_reload_ticks.png");
    public static final Holder<WandAttr> ATTR_BASIC_DELAY_TICKS = register(RegistryNames.WAND_BASIC_DELAY_TICKS,0, 20, -20, "textures/wand_attr/basic_delay_ticks.png");
    public static final Holder<WandAttr> ATTR_MAX_SLOTS = register(RegistryNames.WAND_MAX_SLOTS, 3, 3, 26, "textures/wand_attr/max_slots.png");
    public static final Holder<WandAttr> ATTR_MANA = register(RegistryNames.WAND_MANA, 50);
    public static final Holder<WandAttr> ATTR_ACCUMULATED_RELOAD_TICKS = register(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS);
    public static final Holder<WandAttr> ATTR_REMAINING_DELAY_TICKS = register(RegistryNames.WAND_REMAINING_DELAY_TICKS);
    public static final Holder<WandAttr> ATTR_REMAINING_RELOAD_TICKS = register(RegistryNames.WAND_REMAINING_RELOAD_TICKS);
    public static final Holder<WandAttr> ATTR_LAST_DELAY_TICKS = register(RegistryNames.WAND_LAST_DELAY_TICKS);
    public static final Holder<WandAttr> ATTR_LAST_RELOAD_TICKS = register(RegistryNames.WAND_LAST_RELOAD_TICKS);

    private static Holder<WandAttr> register(RegistryNames name,int defaultValue, int minValue, int maxValue, String texture) {
        return WAND_ATTR_REGISTRAR.register(name.getPath(), () -> new RangedWandAttr(name.get(), defaultValue, minValue, maxValue, RegistryNames.getRL(texture), (nextId++)));
    }
    private static Holder<WandAttr> register(RegistryNames name, int value) {
        return WAND_ATTR_REGISTRAR.register(name.getPath(), () -> new WandAttr(name.get(), value));
    }
    private static Holder<WandAttr> register(RegistryNames name) {
        return register(name, 0);
    }
}
