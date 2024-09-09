package com.taikuus.luomuksia.api.wand;

import com.taikuus.luomuksia.api.entity.AbstractModifiableProj;
import com.taikuus.luomuksia.common.actions.modifier.AbstractModifierAction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ShotState {
    private final List<Entity> projList = new ArrayList<>();
    private final List<AbstractModifierAction> modifierList = new ArrayList<>();
    private final int numFirstDraw;
    private final Player player;
    private final Level world;

    public ShotState(int numFirstDraw, Level world, Player player) {
        this.numFirstDraw = numFirstDraw;
        this.player = player;
        this.world = world;
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
     * @param modifier
     */
    public void addModifier(AbstractModifierAction modifier) {
        modifierList.add(modifier);
    }

    /**
     * Apply all modifiers to the projectiles and shoot them, adding them to the world
     */
    public void applyModifiersAndShoot() {
        for (Entity proj : projList) {
            if (proj instanceof AbstractModifiableProj modProj) {
                for (AbstractModifierAction modifier : modifierList) {
                    modProj.applyModifier(modifier);
                }
            }
        }
        for (Entity proj : projList) {
            world.addFreshEntity(proj);
        }
    }
}
