package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.capability.LocalHurtCooldownCap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class CapAndAttachmentRegistry {
    // Create the DeferredRegister for attachment types
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Luomuksia.MODID);

    public static final Supplier<AttachmentType<LocalHurtCooldownCap.SerializableCooldownMap>> LOCAL_HURT_ATTACHMENT =
            ATTACHMENT_TYPES.register("local_hurt_cooldown_cap",
                    () -> AttachmentType.serializable(LocalHurtCooldownCap.SerializableCooldownMap::new)
                            .copyOnDeath()
                            .build());

    public static final EntityCapability<LocalHurtCooldownCap, Void> LOCAL_HURT_COOLDOWN_CAP = EntityCapability.createVoid(
            RegistryNames.getRL("local_hurt_cooldown_cap"), LocalHurtCooldownCap.class);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (EntityType<? extends Entity> type : BuiltInRegistries.ENTITY_TYPE) {
            if (type.getBaseClass().isAssignableFrom(LivingEntity.class)) {
                event.registerEntity(LOCAL_HURT_COOLDOWN_CAP, type,
                        (livingEntity, ctx) -> new LocalHurtCooldownCap((LivingEntity) livingEntity));
            }
        }
    }
}
