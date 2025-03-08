package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.ITerrainDestroyingModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileLightBlade;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

import java.util.Map;

public class ActionLightBlade extends AbstractProjAction<ProjectileLightBlade>{
    public static final ActionLightBlade INSTANCE = new ActionLightBlade();
    public ActionLightBlade(){
        super(RegistryNames.ACTION_PROJ_LIGHTBLADE.get());
        setNumericShowables(Map.of(
                TooltipShowableStats.PROJECTILE_SPEED, 30,
                TooltipShowableStats.PROJECTILE_INACCURACY, 0.1,
                TooltipShowableStats.DAMAGE_TYPE_MOB_PROJ, 1.0,
                TooltipShowableStats.MANA_COST, 10,
                TooltipShowableStats.CAST_DELAY, 2,
                TooltipShowableStats.RELOAD_TICKS, 6
        ));
    }

    @Override
    public void play(WandContext context, ShotStates stats) {
        super.play(context, stats);
        addProjConfigured(context, stats);
    }

    @Override
    public ProjectileLightBlade relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        ProjectileLightBlade proj = new ProjectileLightBlade(
                player,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
        proj.applyModifier(new ITerrainDestroyingModifier() {
            @Override
            public void applyModifier(AbstractModifiableProj proj) {
                proj.modifiersHelper.addHook(this);
            }
            @Override
            public float getTerrainDestroyingValue(AbstractModifiableProj proj, HitResult result) {
                return 0.1F;
            }
        });
        return proj;
    }
}
