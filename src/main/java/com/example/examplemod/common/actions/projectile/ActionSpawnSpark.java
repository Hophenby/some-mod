package com.example.examplemod.common.actions.projectile;

import com.example.examplemod.RegistryNames;
import com.example.examplemod.api.actions.AbstractWandAction;
import com.example.examplemod.api.wand.ShotState;
import com.example.examplemod.api.wand.WandContext;
import com.example.examplemod.common.entity.projectile.ProjectileSpark;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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
        Vec3 look = player.getLookAngle();
        spark.shoot(look.x, look.y, look.z, 1.5f, 0.0f);
        stats.addProj(spark);
    }
}
