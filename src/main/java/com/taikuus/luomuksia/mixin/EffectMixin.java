package com.taikuus.luomuksia.mixin;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectOrbit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EffectOrbit.class)
public abstract class EffectMixin {

    @Redirect(method = "onResolveBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean captureSpellEntityFromBlock(Level world, Entity entity) {
        if (entity instanceof EntityProjectileSpell spell){
        }
        return world.addFreshEntity(entity);
    }
    @Redirect(method = "onResolveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean captureSpellEntityFromEntity(Level world, Entity entity) {
        if (entity instanceof EntityProjectileSpell spell){
        }
        return world.addFreshEntity(entity);
    }
}
