package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileBouncingBall;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class ActionBouncingBall extends AbstractProjAction<ProjectileBouncingBall> {
    public static final ActionBouncingBall INSTANCE = new ActionBouncingBall();
    public ActionBouncingBall() {
        super(RegistryNames.ACTION_PROJ_BOUNCING.get());
        setNumericShowables(Map.of(
                TooltipShowableStats.PROJECTILE_SPEED, 30,
                TooltipShowableStats.PROJECTILE_INACCURACY, 3.1,
                TooltipShowableStats.DAMAGE_TYPE_MOB_PROJ, 1.0,
                TooltipShowableStats.MANA_COST, 10,
                TooltipShowableStats.CAST_DELAY, 2,
                TooltipShowableStats.RELOAD_TICKS, 6
        ));
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        addDelayAndReload(context);
        stats.addProj(() -> {
            var proj = relatedProjectile(context, stats);
            proj.setInitMotion(stats.getPlayer().getLookAngle(),
                    this.getNumericShowable(TooltipShowableStats.PROJECTILE_SPEED).floatValue() / 20F);
            proj.addInaccuracy(this.getInaccuracy());
            return proj;
        }, 2);
    }
    @Override
    public ProjectileBouncingBall relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        return new ProjectileBouncingBall(
                player,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
    }
}
