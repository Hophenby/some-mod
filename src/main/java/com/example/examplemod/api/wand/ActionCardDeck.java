package com.example.examplemod.api.wand;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A deck of actions that can be drawn from
 * @param actions
 */
public record ActionCardDeck(List<WrappedWandAction> actions){
    public static final Codec<ActionCardDeck> CODEC = WrappedWandAction.CODEC.listOf().xmap(ActionCardDeck::new, ActionCardDeck::getActions);
    public static final StreamCodec<FriendlyByteBuf, ActionCardDeck> STREAM = StreamCodec.of(
            (buf, deck) -> buf.writeCollection(deck.getActions(), WrappedWandAction.STREAM),
            (buf) -> {
                int size = buf.readVarInt();
                List<WrappedWandAction> actions = new CopyOnWriteArrayList<>();
                for (int i = 0; i < size; i++) {
                    actions.add(WrappedWandAction.STREAM.decode(buf));
                }
                return new ActionCardDeck(actions);
            }
    );

    public List<WrappedWandAction> getActions() {
        return actions;
    }
    public void orderDeck() {
        // order the deck using the order of the WrappedGunAction
        actions.sort(Comparator.comparingInt(WrappedWandAction::order));
    }
    public void draw(ActionCardDeck deck) {
        this.actions.addAll(deck.getActions());
    }

    public void draw(List<WrappedWandAction> actions) {
        this.actions.addAll(actions);
    }
    public void draw(WrappedWandAction action) {
        this.actions.add(action);
    }

    /**
     * Draw actions from the deck in the order they are in the deck
     * @param index the index of the action to draw, this index is the order of the list (java.util.List)
     */
    public WrappedWandAction remove(int index) {
        return actions.remove(index);
    }
    public void clear() {
        actions.clear();
    }
    /**
     * Get the action at the index
     * @param index this index is the order of the action (declared in the WrappedGunAction)
     */
    @Nullable
    public WrappedWandAction get(int index) {
        //find the action at the index wrapped in a WrappedGunAction
        for (WrappedWandAction action : actions) {
            if (action.order() == index) {
                return action;
            }
        }
        // if the action is not found return null
        return null;
    }
    public boolean isEmpty() {
        return actions.isEmpty();
    }
    public ActionCardDeck copy() {
        return new ActionCardDeck(new CopyOnWriteArrayList<>(actions));
    }

}
