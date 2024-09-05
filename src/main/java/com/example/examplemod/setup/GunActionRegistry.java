package com.example.examplemod.setup;

import com.example.examplemod.api.wand.IWandAction;
import com.example.examplemod.common.actions.TestAction;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GunActionRegistry {
    private static final ConcurrentHashMap<ResourceLocation, IWandAction> REGISTRIES = new ConcurrentHashMap<>();
    public static void register(IWandAction action, ResourceLocation id) {
        REGISTRIES.put(id, action);
    }
    private static void register(IWandAction action) {
        register(action, action.getId());
    }
    public static IWandAction get(ResourceLocation id) {
        return REGISTRIES.get(id);
    }
    public static Map<ResourceLocation, IWandAction> getAllRegistries() {
        return REGISTRIES;
    }
    public static void setup() {
        //register all the actions
        register(TestAction.INSTANCE);
    }
}
