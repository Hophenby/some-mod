package com.taikuus.luomuksia.api.wand;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

import static com.taikuus.luomuksia.Luomuksia.LOGGER;

public class WandContext {
    private final ActionCardDeck deck = new ActionCardDeck(new ArrayList<>());
    private final ActionCardDeck hand = new ActionCardDeck(new ArrayList<>());
    private final ActionCardDeck discard = new ActionCardDeck(new ArrayList<>());
    private int storedMana;
    private int reloadTicks;
    private boolean startReload = false;
    private int delayTicks;
    private ShotStates currentState;
    private boolean disableActionDrawing = false;

    public WandContext(ActionCardDeck rawDeck, int storedMana, int reloadTicks) {
        this.deck.draw(rawDeck.actions());
        this.storedMana = storedMana;
        this.reloadTicks = reloadTicks;
        this.delayTicks = 0;
    }
    public WandContext(ActionCardDeck deck, ActionCardDeck hand, ActionCardDeck discard, int storedMana, int reloadTicks) {
        this.deck.draw(deck.actions());
        this.hand.draw(hand.actions());
        this.discard.draw(discard.actions());
        this.storedMana = storedMana;
        this.reloadTicks = reloadTicks;
        this.delayTicks = 0;
    }
    public ShotStates createChildState(int numFirstDraw, Level world, Player player, InteractionHand hand) {
        ShotStates state = new ShotStates(numFirstDraw, world, player);
        parseShot(state);
        return state;
    }
    /**
     * Parse the shot
     * Returned state may be used at "triggered shot"
     * @param state the state before the shot is parsed
     * @return the state after the shot is parsed
     */
    public ShotStates parseShot(ShotStates state) {
        currentState = state;
        drawActions(state.getNumFirstDraw());
        return state;
    }
    public void shoot(Level world, Player player, InteractionHand pHand, IWand wand) {
        if (world.isClientSide) {
            return;
        }
        currentState = createChildState(1, world, player, pHand);
        LOGGER.info("deck: " + deck.actions().size() + " hand: " + hand.actions().size() + " discard: " + discard.actions().size());
        LOGGER.info("storedMana: " + storedMana + " reloadTicks: " + reloadTicks + " delayTicks: " + delayTicks);
        moveHandToDiscard();
        if (deck.isEmpty() || startReload) {
            startReload = true;
            moveDiscardToDeck();
            orderDeck();
        }
        addProjectilesToWorld(currentState);
        LOGGER.info("deck: " + deck.actions().size() + " hand: " + hand.actions().size() + " discard: " + discard.actions().size());
        LOGGER.info("storedMana: " + storedMana + " reloadTicks: " + reloadTicks + " delayTicks: " + delayTicks);
        wand.afterShot(this, world, player, pHand);
    }

    /**
     * Add projectiles to the world
     * Called after the shot is parsed
     * @param state the state after the shot is parsed
     */
    public static void addProjectilesToWorld(ShotStates state) {
        state.applyModifiersAndShoot();
    }
    public void moveDiscardToDeck() {
        deck.draw(discard.actions());
        discard.actions().clear();
    }
    public void moveHandToDiscard() {
        discard.draw(hand.actions());
        hand.actions().clear();
    }
    public void orderDeck() {
        deck.orderDeck();

    }
    private Boolean drawAndCast(){
        WrappedWandAction wrappedAction = null;
        if (deck.isEmpty()) {
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
            if(!disableActionDrawing && !drawAndCast()) while (!deck.isEmpty()) if (drawAndCast()) break;
        }
    }
    public void disableActionDrawing() {
        disableActionDrawing = true;
    }
    public void enableActionDrawing() {
        disableActionDrawing = false;
    }

    private void castAction(WrappedWandAction action) {
        // move action to hand
        hand.draw(action);
        // cast action
        action.action().action(this, currentState);
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

    public ActionCardDeck getDeck() {
        return deck;
    }
    public ActionCardDeck getHand() {
        return hand;
    }
    public ActionCardDeck getDiscard() {
        return discard;
    }
    public Getters getGetters() {
        return new Getters(this);
    }

    /**
     * Getters for the WandContext
     * Used to get the wandData from the context at the end of the shot
     */
    public static class Getters {
        private final WandContext context;
        public Getters(WandContext context) {
            this.context = context;
        }
        public ActionCardDeck getDeck() {
            return context.deck;
        }
        public ActionCardDeck getHand() {
            return context.hand;
        }
        public ActionCardDeck getDiscard() {
            return context.discard;
        }
        public int getStoredMana() {
            return Math.clamp(context.storedMana, 0, Integer.MAX_VALUE);
        }
        public int getReloadTicks() {
            return Math.clamp(context.reloadTicks, 0, Integer.MAX_VALUE);
        }
        public int getDelayTicks() {
            return Math.clamp(context.delayTicks, 0, Integer.MAX_VALUE);
        }
        public boolean getStartReload() {
            return context.startReload;
        }
        public ShotStates getState() {
            return context.currentState;
        }
    }
}
