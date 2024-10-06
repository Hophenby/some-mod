package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IOnHitModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ModifierExplosiveHit extends AbstractModifierAction implements IOnHitModifier {
    public static final ModifierExplosiveHit INSTANCE = new ModifierExplosiveHit();
    public ModifierExplosiveHit() {
        super(RegistryNames.ACTION_MODIFIER_EXPLOSIVE_HIT.get());
        setNumericShowable(TooltipShowableStats.MANA_COST, 70);
        setNumericShowable(TooltipShowableStats.EXPLOSION_LEVEL, 2);
    }
    /*
     * Explosion enums: See -> net/minecraft/world/level/Level.java:761
     */
    @Override
    public void onHit(AbstractModifiableProj proj, HitResult result){
        proj.level().explode(proj, proj.position().x, proj.position().y, proj.position().z, 2.0f, Level.ExplosionInteraction.NONE);
    }
}
