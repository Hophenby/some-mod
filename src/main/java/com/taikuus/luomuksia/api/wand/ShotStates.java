package com.taikuus.luomuksia.api.wand;

import com.taikuus.luomuksia.api.actions.IModifierAction;
import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ShotStates {
    private final List<Entity> projList = new ArrayList<>();
    private final List<IModifierAction> modifierList = new ArrayList<>();
    private final int numFirstDraw;
    private final Player player;
    private final Level world;

    public ShotStates(int numFirstDraw, Level world, Player player) {
        this.numFirstDraw = numFirstDraw;
        this.player = player;
        this.world = world;
    }
    public ShotStates childState() {
        return new ShotStates(numFirstDraw, world, player);
    }
    public Player getPlayer() {
        return player;
    }
    public Level getWorld() {
        return world;
    }
    public int getNumFirstDraw() {
        return numFirstDraw;
    }

    public List<Entity> getProjList() {
        return projList;
    }
    public Entity lastProj() {
        return projList.get(projList.size() - 1);
    }

    /**
     * Add a projectile to the list of projectiles.
     * The initial properties of the projectile should be set before calling this method
     * @param proj the projectile to add
     */
    public void addProj(Entity proj) {
        projList.add(proj);
    }

    /**
     * Add a modifier INSTANCE to the list of modifiers
     *
     * @param modifier the modifier to add
     */
    public void addModifier(IModifierAction modifier) {
        modifierList.add(modifier);
    }

    /**
     * Apply all modifiers to the projectiles and shoot them, adding them to the world
     */
    public void applyModifiersAndShoot() {
        for (Entity proj : projList) {
            if (proj instanceof AbstractModifiableProj modProj) {
                for (IModifierAction modifier : modifierList) {
                    modProj.applyModifier(modifier);
                }
                modProj.shoot();
            }
        }
        for (Entity proj : projList) {
            world.addFreshEntity(proj);
        }
    }
}
