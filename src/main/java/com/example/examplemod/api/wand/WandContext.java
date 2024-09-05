package com.example.examplemod.api.wand;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

import static com.example.examplemod.ExampleMod.LOGGER;

public class WandContext {
    private final ActionCardDeck deck = new ActionCardDeck(new ArrayList<>());
    private final ActionCardDeck hand = new ActionCardDeck(new ArrayList<>());
    private final ActionCardDeck discard = new ActionCardDeck(new ArrayList<>());
    private int storedMana;
    private int reloadTicks;
    private boolean startReload = false;
    private int delayTicks;
    private ShotState state;

    public WandContext(ActionCardDeck rawDeck, int storedMana, int reloadTicks, int delayTicks) {
        this.deck.draw(rawDeck.getActions());
        this.storedMana = storedMana;
        this.reloadTicks = reloadTicks;
        this.delayTicks = delayTicks;
    }
    public WandContext(ActionCardDeck deck, ActionCardDeck hand, ActionCardDeck discard, int storedMana, int reloadTicks, int delayTicks) {
        this.deck.draw(deck.getActions());
        this.hand.draw(hand.getActions());
        this.discard.draw(discard.getActions());
        this.storedMana = storedMana;
        this.reloadTicks = reloadTicks;
        this.delayTicks = delayTicks;
    }
    public ShotState createChildState(int numFirstDraw,Level world, Player player, InteractionHand hand) {
        ShotState state = new ShotState(numFirstDraw, world, player);
        parseShot(state);
        return state;
    }
    public ShotState parseShot(ShotState state) {
        drawActions(state.getNumFirstDraw());
        return state;
    }
    public void shoot(Level world, Player player, InteractionHand pHand, IWand wand) {
        if (world.isClientSide) {
            return;
        }
        state = createChildState(1, world, player, pHand);
        LOGGER.info("deck: " + deck.getActions().size() + " hand: " + hand.getActions().size() + " discard: " + discard.getActions().size());
        LOGGER.info("storedMana: " + storedMana + " reloadTicks: " + reloadTicks + " delayTicks: " + delayTicks);
        moveHandToDiscard();
        if (deck.isEmpty() || startReload) {
            startReload = true;
            moveDiscardToDeck();
            orderDeck();
        }

        wand.afterShot(this, world, player, pHand);
    }
    public static void addProjectilesToWorld(Level world, ShotState state) {
        for (var proj : state.getProjList()) {
            world.addFreshEntity(proj);
        }
    }
    public void moveDiscardToDeck() {
        deck.draw(discard.getActions());
        discard.getActions().clear();
    }
    public void moveHandToDiscard() {
        discard.draw(hand.getActions());
        hand.getActions().clear();
    }
    public void orderDeck() {
        deck.orderDeck();

    }
    private Boolean drawAndCast(){
        WrappedWandAction wrappedAction = null;
        if (hand.isEmpty()) {
            moveDiscardToDeck();
            orderDeck();
            startReload = true;
        }
        if (!deck.isEmpty()) {
            // draw from the start of the deck
            wrappedAction = deck.remove(0);
            // check if mana is enough
            if (!checkMana(wrappedAction.action().getManaCost())) {
                discard.draw(wrappedAction);
                return false;
            }
            spendMana(wrappedAction.action().getManaCost());
            LOGGER.debug("Casting action: " + wrappedAction.action().getId() + " with mana cost: " + wrappedAction.action().getManaCost());
            LOGGER.debug("Remaining mana: " + storedMana);
        }
        if (wrappedAction != null) {
            castAction(wrappedAction);
        }

        return true;
    }
    public void drawActions(int num) {
        for (int i = 0; i < num; i++) {
            if(!drawAndCast()) while (!deck.isEmpty()) if (drawAndCast()) break;
        }
    }

    private void castAction(WrappedWandAction action) {
        // move action to hand
        hand.draw(action);
        // cast action
        action.action().action(this, state);
    }

    public Boolean checkMana(int cost) {
        return storedMana >= cost;
    }
    public void spendMana(int cost) {
        storedMana -= cost;
    }
    public void addReloadTicks(int ticks) {
        reloadTicks += ticks;
    }
    public void addDelayTicks(int ticks) {
        delayTicks += ticks;
    }
    public int getReloadTicks() {
        return reloadTicks;
    }
    public int getDelayTicks() {
        return delayTicks;
    }
    public void setStartReload(Boolean startReload) {
        this.startReload = startReload;
    }
    public Getters getGetters() {
        return new Getters();
    }
    public class Getters {
        public ActionCardDeck getDeck() {
            return deck;
        }
        public ActionCardDeck getHand() {
            return hand;
        }
        public ActionCardDeck getDiscard() {
            return discard;
        }
        public int getStoredMana() {
            return storedMana;
        }
        public int getReloadTicks() {
            return reloadTicks;
        }
        public int getDelayTicks() {
            return delayTicks;
        }
        public ShotState getState() {
            return state;
        }
    }
}
