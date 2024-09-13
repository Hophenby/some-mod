package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileStoneCutter;
import net.minecraft.world.entity.player.Player;

public class ActionSpawnStoneCutter extends AbstractProjAction{
    public static final ActionSpawnStoneCutter INSTANCE = new ActionSpawnStoneCutter();
    public ActionSpawnStoneCutter() {
        super(RegistryNames.ACTION_PROJ_SC.get());
    }

    @Override
    public AbstractModifiableProj relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        ProjectileStoneCutter stoneCutter = new ProjectileStoneCutter(
                player,
                player.getX(),
                player.getY() + stats.getPlayer().getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
        stoneCutter.setInitMotion(player.getLookAngle(), 1.5f, 0.3f);
        return stoneCutter;
    }

}
