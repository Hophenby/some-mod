package com.example.examplemod.setup;

import com.example.examplemod.api.wand.AbstractWandAction;
import com.example.examplemod.common.actions.TestAction;
import com.example.examplemod.common.actions.modifier.ModifierAccelerate;
import com.example.examplemod.common.actions.projectile.ActionSpawnSpark;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GunActionRegistry {
    private static final ConcurrentHashMap<ResourceLocation, AbstractWandAction> REGISTRIES = new ConcurrentHashMap<>();
    public static void register(AbstractWandAction action, ResourceLocation id) {
        REGISTRIES.put(id, action);
    }
    private static void register(AbstractWandAction action) {
        register(action, action.getId());
    }
    public static AbstractWandAction get(ResourceLocation id) {
        return REGISTRIES.get(id);
    }
    public static Map<ResourceLocation, AbstractWandAction> getAllRegistries() {
        return REGISTRIES;
    }
    public static void setup() {
        //register all the actions
        register(TestAction.INSTANCE);
        register(ModifierAccelerate.INSTANCE);
        register(ActionSpawnSpark.INSTANCE);
    }
}
