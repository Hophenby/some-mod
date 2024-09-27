package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.utils.ProjUtils;
import net.minecraft.world.phys.Vec3;

public class ModifierRedirectingHoming extends AbstractModifierAction implements IMotionModifier {
    public static final ModifierRedirectingHoming INSTANCE = new ModifierRedirectingHoming();
    public ModifierRedirectingHoming() {
        super(RegistryNames.ACTION_MODIFIER_REDIRECTING_HOMING.get());
        setNumericShowable(TooltipShowableStats.MANA_COST, 60);
    }
    @Override
    public Vec3 applyMotivePerTick(AbstractModifiableProj proj, Vec3 motion) {
        Vec3 target = ProjUtils.findTarget(proj.position(), proj.level(), 10, proj.piercing);
        if (target != null) {
            Vec3 direction = target.subtract(proj.position()).normalize();
            return direction.scale(motion.length()); // Redirects the projectile to the target
        }
        return motion;
    }
}
