package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.common.actions.modifier.*;
import com.taikuus.luomuksia.common.actions.multicast.ActionMulticast;
import com.taikuus.luomuksia.common.actions.multicast.ActionScatteredMulticast;
import com.taikuus.luomuksia.common.actions.other.ActionAddTrigger;
import com.taikuus.luomuksia.common.actions.other.TestAction;
import com.taikuus.luomuksia.common.actions.projectile.ActionBouncingBall;
import com.taikuus.luomuksia.common.actions.projectile.ActionSpark;
import com.taikuus.luomuksia.common.actions.projectile.ActionStoneCutter;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WandActionRegistry {
    private static final ConcurrentHashMap<ResourceLocation, AbstractWandAction> REGISTRIES = new ConcurrentHashMap<>();
    public static void register(AbstractWandAction action, ResourceLocation id) {
        if (REGISTRIES.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate action id: " + id);
        }
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
        register(ModifierHoming.INSTANCE);
        register(ModifierLight.INSTANCE);
        register(ModifierRedirectingHoming.INSTANCE);
        register(ModifierExplosiveHit.INSTANCE);
        register(ActionSpark.INSTANCE);
        register(ActionStoneCutter.INSTANCE);
        register(ActionBouncingBall.INSTANCE);
        register(ActionMulticast.INSTANCEx2);
        register(ActionMulticast.INSTANCEx3);
        register(ActionMulticast.INSTANCEx4);
        register(ActionMulticast.INSTANCEx8);
        register(ActionScatteredMulticast.INSTANCE);
        register(ActionAddTrigger.INSTANCE);
    }
}
