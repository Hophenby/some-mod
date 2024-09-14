package com.taikuus.luomuksia.api.event;

import com.taikuus.luomuksia.api.wand.ShotStates;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class WandFireEvent extends LivingEvent {
    private final ShotStates states;
    private final Player player;
    private final Level world;
    private final InteractionHand hand;
    private final ItemStack wand;
    private WandFireEvent(ShotStates states, Player player, Level world, InteractionHand hand, ItemStack wand) {
        super(player);
        this.states = states;
        this.player = player;
        this.world = world;
        this.hand = hand;
        this.wand = wand;
    }

    public ItemStack getWand() {
        return wand;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public Level getLevel() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public ShotStates getStates() {
        return states;
    }

    /**
     * Called after the player uses a wand and before the shot is parsed.
     * (before the {@link com.taikuus.luomuksia.api.wand.WandContext#parseShot(com.taikuus.luomuksia.api.wand.ShotStates)} is called).
     * This event is cancellable.
     */
    public static class Pre extends WandFireEvent implements ICancellableEvent {
        public Pre(ShotStates states, Player player, Level world, InteractionHand hand, ItemStack wand) {
            super(states, player, world, hand, wand);
        }
    }

    /**
     * Called after the shot is parsed and before the shot is executed.
     * (before the {@link com.taikuus.luomuksia.api.wand.WandContext#addProjectilesToWorld(com.taikuus.luomuksia.api.wand.ShotStates)} is called).
     */
    public static class Post extends WandFireEvent {
        public Post(ShotStates states, Player player, Level world, InteractionHand hand, ItemStack wand) {
            super(states, player, world, hand, wand);
        }
    }
}
