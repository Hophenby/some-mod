package com.taikuus.luomuksia.event;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.capability.LocalHurtCooldownCap;
import com.taikuus.luomuksia.api.utils.DamageUtils;
import com.taikuus.luomuksia.setup.CapAndAttachmentRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Luomuksia.MODID)
public class EntityHurtEvent {
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity entity && !entity.level().isClientSide){
            LocalHurtCooldownCap cap = entity.getCapability(CapAndAttachmentRegistry.LOCAL_HURT_COOLDOWN_CAP);
            if (cap == null) return;
            cap.tickCooldowns();
        }
    }
    @SubscribeEvent
    public static void onEntityHurt(LivingIncomingDamageEvent event) {
        if (!event.getEntity().level().isClientSide){
            LocalHurtCooldownCap cap = event.getEntity().getCapability(CapAndAttachmentRegistry.LOCAL_HURT_COOLDOWN_CAP);
            if (cap == null) return;
            Entity directEntity = event.getSource().getDirectEntity();
            if (directEntity == null) return;
            if (!cap.canHurt(directEntity)) event.setCanceled(true);
            else cap.setHurtCooldown(directEntity, DamageUtils.getEntityHitCooldown(directEntity));
        }

    }
}
