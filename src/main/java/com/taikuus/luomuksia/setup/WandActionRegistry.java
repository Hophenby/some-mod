package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.common.actions.other.ActionAddTrigger;
import com.taikuus.luomuksia.common.actions.other.TestAction;
import com.taikuus.luomuksia.common.actions.modifier.ModifierAccelerate;
import com.taikuus.luomuksia.common.actions.multicast.ActionMulticast;
import com.taikuus.luomuksia.common.actions.multicast.ActionScatteredMulticast;
import com.taikuus.luomuksia.common.actions.projectile.ActionSpawnSpark;
import com.taikuus.luomuksia.common.actions.projectile.ActionSpawnStoneCutter;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WandActionRegistry {
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
        register(ActionSpawnStoneCutter.INSTANCE);
        register(ActionMulticast.INSTANCEx2);
        register(ActionMulticast.INSTANCEx3);
        register(ActionMulticast.INSTANCEx4);
        register(ActionMulticast.INSTANCEx8);
        register(ActionScatteredMulticast.INSTANCE);
        register(ActionAddTrigger.INSTANCE);
    }
}
