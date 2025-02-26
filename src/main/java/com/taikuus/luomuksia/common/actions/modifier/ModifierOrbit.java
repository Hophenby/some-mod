package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.api.utils.ProjUtils;
import net.minecraft.world.phys.Vec3;

public class ModifierOrbit extends AbstractModifierAction implements IMotionModifier {
    public static final ModifierOrbit INSTANCE = new ModifierOrbit();
    public ModifierOrbit() {
        super(RegistryNames.ACTION_MODIFIER_ORBIT.get());
        setNumericShowable(TooltipShowableStats.MANA_COST, 60);
    }

    @Override
    public void applyModifier(AbstractModifiableProj proj) {
        super.applyModifier(proj);
        proj.setNoGravity(true);
    }
    @Override
    public Vec3 applyMotivePerTick(AbstractModifiableProj proj, Vec3 motion) {
        if (proj.getOrbittingEntity() == null) {
            // find a target to orbit
            proj.setOrbittingEntity(ProjUtils.findEntityTarget(proj.position(), proj.level(), 2, true));
            return motion;
        }
        if (proj.getOrbittingEntity().isRemoved()
                || proj.timer < 2 // wait for the entity to be away from the player
        ) {
            return motion;
        }
        motion.multiply(1, 0.8, 1);
        return proj.getOrbittingEntity().position()
                .add(1E-7, 0.25, 1E-7)
                .subtract(proj.position())
                .normalize()
                .yRot((float) (-1 / 2f * Math.PI));
    }
}
