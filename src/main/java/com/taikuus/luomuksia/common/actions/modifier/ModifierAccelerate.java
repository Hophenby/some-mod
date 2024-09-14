package com.taikuus.luomuksia.common.actions.modifier;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.IMotionModifier;
import com.taikuus.luomuksia.common.item.WandActionItem;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import net.minecraft.world.phys.Vec3;

public class ModifierAccelerate extends AbstractModifierAction implements IMotionModifier {
    public static final ModifierAccelerate INSTANCE = new ModifierAccelerate();
    public ModifierAccelerate() {
        super(RegistryNames.ACTION_MODIFIER_ACCELERATE.get());
    }

    @Override
    public Vec3 applyPerTick(AbstractModifiableProj proj, Vec3 motion) {
        return proj.timer > 30 ? motion : motion.add(motion.normalize().scale(0.1f));
    }
    @Override
    public void action(WandContext context, ShotStates stats) {
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
