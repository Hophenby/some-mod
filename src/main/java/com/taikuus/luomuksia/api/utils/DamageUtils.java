package com.taikuus.luomuksia.api.utils;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class DamageUtils {
    public static int getEntityHitCooldown(@NotNull Entity entity){
        return VanillaEntityHitCooldown.getCooldown(entity);
    }
    public enum VanillaEntityHitCooldown{
        ARROW(EntityType.ARROW, 20),
        SPECTRAL_ARROW(EntityType.SPECTRAL_ARROW, 20),
        ;
        private final EntityType<?> entityType;
        private final int cooldown;
        VanillaEntityHitCooldown(EntityType<?> entityType, int cooldown){
            this.entityType = entityType;
            this.cooldown = cooldown;
        }
        private static int getCooldown(@NotNull Entity entity){
            for (VanillaEntityHitCooldown cooldown : values()){
                if (cooldown.entityType.equals(entity.getType())){
                    return cooldown.cooldown;
                }
            }
            if (entity instanceof AbstractModifiableProj modProj){
                return modProj.getLocalHitCooldown();
            }
            return 0;
        }
    }
}
