package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileBouncingBall;
import net.minecraft.world.entity.player.Player;

public class ActionSpawnBouncingBall extends AbstractProjAction {
    public static final ActionSpawnBouncingBall INSTANCE = new ActionSpawnBouncingBall();
    public ActionSpawnBouncingBall() {
        super(RegistryNames.ACTION_PROJ_BOUNCING.get());
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        context.addDelayTicks(-4);
        stats.addProj(() -> relatedProjectile(context, stats), 2);
    }

    @Override
    public AbstractModifiableProj relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        ProjectileBouncingBall proj = new ProjectileBouncingBall(
                player,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
        proj.setInitMotion(player.getLookAngle(), 1.5f, 3.1f);
        return proj;
    }
}
