package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileSpark;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class ActionSpark extends AbstractProjAction<ProjectileSpark> {
    public static final ActionSpark INSTANCE = new ActionSpark();
    public ActionSpark() {
        super(RegistryNames.ACTION_PROJ_SPARK.get());
        setNumericShowables(Map.of(
                TooltipShowableStats.PROJECTILE_SPEED, 30,
                TooltipShowableStats.PROJECTILE_INACCURACY, 0.3,
                TooltipShowableStats.DAMAGE_TYPE_MOB_PROJ, 1.0,
                TooltipShowableStats.MANA_COST, 5,
                TooltipShowableStats.CAST_DELAY, 4,
                TooltipShowableStats.RELOAD_TICKS, 4
        ));
    }

    @Override
    public ProjectileSpark relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        return new ProjectileSpark(
                player,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
    }

}
