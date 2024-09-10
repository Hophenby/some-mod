package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ShotState;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileStoneCutter;
import net.minecraft.world.entity.player.Player;

public class ActionSpawnStoneCutter extends AbstractWandAction {
    public static final ActionSpawnStoneCutter INSTANCE = new ActionSpawnStoneCutter();
    public ActionSpawnStoneCutter() {
        super(RegistryNames.ACTION_PROJ_SC.get(), "spawn_stonecutter","Spawn Stonecutter");
    }

    @Override
    public void action(WandContext context, ShotState stats) {
        Player player = stats.getPlayer();
        ProjectileStoneCutter stoneCutter = new ProjectileStoneCutter(
                player,
                player.getX(),
                player.getY() + stats.getPlayer().getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
        stoneCutter.setShootState(player.getLookAngle(), 1.5f, 0.3f);
        stats.addProj(stoneCutter);
    }
}
