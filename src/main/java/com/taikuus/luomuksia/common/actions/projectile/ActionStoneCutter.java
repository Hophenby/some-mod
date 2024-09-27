package com.taikuus.luomuksia.common.actions.projectile;

import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileStoneCutter;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public class ActionStoneCutter extends AbstractProjAction<ProjectileStoneCutter>{
    public static final ActionStoneCutter INSTANCE = new ActionStoneCutter();
    public ActionStoneCutter() {
        super(RegistryNames.ACTION_PROJ_SC.get());
        setNumericShowables(Map.of(
                TooltipShowableStats.PROJECTILE_SPEED, 30,
                TooltipShowableStats.PROJECTILE_INACCURACY, 0.3,
                TooltipShowableStats.DAMAGE_TYPE_CUTTING, 1.0,
                TooltipShowableStats.MANA_COST, 40
        ));
    }

    @Override
    public ProjectileStoneCutter relatedProjectile(WandContext context, ShotStates stats) {
        Player player = stats.getPlayer();
        return new ProjectileStoneCutter(
                player,
                player.getX(),
                player.getY() + stats.getPlayer().getEyeHeight(),
                player.getZ(),
                stats.getWorld()
        );
    }
}
