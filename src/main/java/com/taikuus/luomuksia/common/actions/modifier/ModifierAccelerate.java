package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.api.actions.WandActionItem;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotState;
import com.taikuus.luomuksia.api.wand.WandContext;
import net.minecraft.world.phys.Vec3;

public class ModifierAccelerate extends AbstractModifierAction implements IMotionModifier {
    public static final ModifierAccelerate INSTANCE = new ModifierAccelerate();
    public ModifierAccelerate() {
        super(RegistryNames.ACTION_MODIFIER_ACCELERATE.get(), "Accelerate", "Accelerates the projectile");
    }

    @Override
    public Vec3 applyPerTick(AbstractModifiableProj proj, Vec3 motion) {
        return motion.add(motion.normalize().multiply(0.1f,0.1f,0.1f));
    }
    @Override
    public void action(WandContext context, ShotState stats) {
        stats.addModifier(INSTANCE);
        context.drawActions(1);
    }

    @Override
    public void applyModifier(AbstractModifiableProj proj) {
        proj.modifiersHelper.addHook(this);
    }

    @Override
    public WandActionItem getHookItem() {
        return INSTANCE.getActionItem();
    }
}
