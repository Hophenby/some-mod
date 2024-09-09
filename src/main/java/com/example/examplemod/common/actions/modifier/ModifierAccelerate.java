package com.example.examplemod.common.actions.modifier;

import com.example.examplemod.RegistryNames;
import com.example.examplemod.api.actions.IMotionModifier;
import com.example.examplemod.api.actions.WandActionItem;
import com.example.examplemod.api.entity.AbstractModifiableProj;
import com.example.examplemod.api.wand.ShotState;
import com.example.examplemod.api.wand.WandContext;
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
