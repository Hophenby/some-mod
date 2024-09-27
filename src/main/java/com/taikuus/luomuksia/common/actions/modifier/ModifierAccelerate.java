package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import net.minecraft.world.phys.Vec3;

public class ModifierAccelerate extends AbstractModifierAction implements IMotionModifier {
    public static final ModifierAccelerate INSTANCE = new ModifierAccelerate();
    public ModifierAccelerate() {
        super(RegistryNames.ACTION_MODIFIER_ACCELERATE.get());
        setNumericShowable(TooltipShowableStats.MANA_COST, 20);
    }

    @Override
    public Vec3 applyMotivePerTick(AbstractModifiableProj proj, Vec3 motion) {
        return proj.timer > 30 ? motion : motion.add(motion.normalize().scale(0.1f));
    }

}
