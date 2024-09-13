package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileSpark;
import net.minecraft.world.entity.player.Player;

public class ActionSpawnSpark extends AbstractProjAction {
    public static final ActionSpawnSpark INSTANCE = new ActionSpawnSpark();
    public ActionSpawnSpark() {
        super(RegistryNames.ACTION_PROJ_SPARK.get());
    }

    @Override
    public AbstractModifiableProj relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        ProjectileSpark proj = new ProjectileSpark(
                player,
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
        proj.setInitMotion(player.getLookAngle(), 1.5f, 0.1f);
        return proj;
    }

    @Override
    public void action(WandContext context, ShotStates stats) {
        context.addDelayTicks(4);
        context.addReloadTicks(4);
        stats.addProj(() -> relatedProjectile(context, stats));
    }
}
