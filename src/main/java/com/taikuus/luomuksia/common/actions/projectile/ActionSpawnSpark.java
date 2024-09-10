package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotState;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileSpark;
import net.minecraft.world.entity.player.Player;

public class ActionSpawnSpark extends AbstractWandAction {
    public static final ActionSpawnSpark INSTANCE = new ActionSpawnSpark();
    public ActionSpawnSpark() {
        super(RegistryNames.ACTION_PROJ_SPARK.get(), "spawn_spark","Spawn Spark");
    }

    @Override
    public void action(WandContext context, ShotState stats) {
        Player player = stats.getPlayer();
        ProjectileSpark spark = new ProjectileSpark(
                player,
                player.getX(),
                player.getY() + stats.getPlayer().getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
        spark.setShootState(player.getLookAngle(), 1.5f, 0.1f);
        stats.addProj(spark);
    }
}
