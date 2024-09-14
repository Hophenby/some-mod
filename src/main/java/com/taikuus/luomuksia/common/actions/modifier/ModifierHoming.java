package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.utils.ProjectileUtils;
import net.minecraft.world.phys.Vec3;

public class ModifierHoming extends AbstractModifierAction implements IMotionModifier {
    public static final ModifierHoming INSTANCE = new ModifierHoming();
    public ModifierHoming() {
        super(RegistryNames.ACTION_MODIFIER_HOMING.get());
    }
    @Override
    public Vec3 applyMotivePerTick(AbstractModifiableProj proj, Vec3 motion) {
        Vec3 target = ProjectileUtils.findTarget(proj.position(), proj.level(), 10, proj.piercing);
        if (target != null) {
            Vec3 direction = target.subtract(proj.position()).normalize();
            return motion.add(direction.scale(0.2f)); // Gives the projectile a slight homing motion
        }
        return motion;
    }
    @Override
    public int getManaCost() {
        return 30;
    }
}
