package com.taikuus.luomuksia.api.wand;

import com.taikuus.luomuksia.api.actions.IModifier;
import com.taikuus.luomuksia.api.entity.proj.AbstractModifiableProj;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ShotStates {
    private final List<Supplier<? extends Entity>> projList = new ArrayList<>();
    private final List<IModifier> modifierList = new ArrayList<>();
    private final int numFirstDraw;
    private final Player player;
    private final Level world;
    private int lastAddedCount = 0;
    private TriFunction<Integer, Integer, Vec3, Vec3> shapingFunction = null;

    public ShotStates(int numFirstDraw, Level world, Player player) {
        this.numFirstDraw = numFirstDraw;
        this.player = player;
        this.world = world;
    }
    public ShotStates childState() {
        return new ShotStates(numFirstDraw, world, player);
    }
    public ShotStates childState(int numFirstDraw) {
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

    public List<Supplier<? extends Entity>> getProjList() {
        return projList;
    }
    public List<Supplier<? extends Entity>> lastProjs() {
        //return projList.get(projList.size() - 1);
        return projList.subList(projList.size() - lastAddedCount, projList.size());
    }

    /**
     * Add a projectile to the list of projectiles.
     * The initial properties of the projectile should be set before calling this method
     * @param projSup the projectile to add
     */
    public void addProj(Supplier<? extends Entity> projSup) {
        projList.add(projSup);
        lastAddedCount = 1;
    }

    @SafeVarargs
    public final void addProj(Supplier<? extends Entity>... projs) {
        Supplier<? extends Entity>[] projsCopy = Arrays.copyOf(projs, projs.length); // copied to prevent varargs corruption
        projList.addAll(Arrays.asList(projsCopy));
        lastAddedCount = projs.length;
    }
    public void addProj(Supplier<? extends Entity> projSup, int count) {
        for (int i = 0; i < count; i++) {
            projList.add(projSup);
        }
        //Luomuksia.LOGGER.debug("Added " + count + " projectiles, total: " + projList.size());
        lastAddedCount = count;
    }

    /**
     * Add a modifier INSTANCE to the list of modifiers
     *
     * @param modifier the modifier to add
     */
    public void addModifier(IModifier modifier) {
        modifierList.add(modifier);
    }

    /**
     * Set the shaping function for the projectiles
     * @param shapingFunc the shaping function
     *                    The function should take an integer index of multiple projectiles and an integer count of multiple projectiles
     *                    Then it should take the deltaMovement of the projectile which is its initial movement
     *                    The function should return the new deltaMovement of the projectile
     */
    public void setShapingFunction(TriFunction<Integer, Integer, Vec3, Vec3> shapingFunc) {
        // only set the shaping function if it is not already set
        if (shapingFunction == null) {
            shapingFunction = shapingFunc;
        }
    }
    /**
     * Apply all modifiers to the projectiles and shoot them, adding them to the world
     */
    public void applyModifiersAndShoot() {
        List<Entity> tempProjList = new ArrayList<>();
        //Luomuksia.LOGGER.debug("Applying modifiers to " + projList.size() + " projectiles");
        int projIndex = 0;
        int projCount = projList.size();
        for (Supplier<? extends Entity> wrappedProj : projList) {
            Entity proj = wrappedProj.get();
            if (proj instanceof AbstractModifiableProj modProj) {
                for (IModifier modifier : modifierList) {
                    modProj.applyModifier(modifier);
                }
                modProj.shoot();
            }
            if (shapingFunction != null) proj.setDeltaMovement(shapingFunction.apply(projIndex, projCount, proj.getDeltaMovement()));
            tempProjList.add(proj);
            projIndex++;
        }
        //Luomuksia.LOGGER.debug("Adding " + tempProjList.size() + " projectiles to the world");
        for (Entity proj : tempProjList) {
            world.addFreshEntity(proj);
        }
    }
}
