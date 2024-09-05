package com.example.examplemod.api.wand;

import com.example.examplemod.RegistryNames;
import com.example.examplemod.common.actions.TestAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WandData {
    public final List<NumericWandAttr> allAttr = List.of(
            new NumericWandAttr(RegistryNames.WAND_MAX_MANA.get(), 100),
            new NumericWandAttr(RegistryNames.WAND_MANA_REGEN.get(), 1),
            new NumericWandAttr(RegistryNames.WAND_BASIC_RELOAD_TICKS.get(), 20),
            new NumericWandAttr(RegistryNames.WAND_BASIC_DELAY_TICKS.get(), 10),
            new NumericWandAttr(RegistryNames.WAND_MAX_ACTION_CARDS.get(), 5),

            new NumericWandAttr(RegistryNames.WAND_MANA.get(), 100),
            new NumericWandAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get(), 0),
            new NumericWandAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get(), 0)
    );
    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(NumericWandAttr.CODEC).fieldOf("allAttr").forGetter(WandData::attrList),
                    ActionCardDeck.CODEC.fieldOf("deck").forGetter(WandData::getDeck),
                    ActionCardDeck.CODEC.fieldOf("hand").forGetter(WandData::getHand),
                    ActionCardDeck.CODEC.fieldOf("discard").forGetter(WandData::getDiscard)
            ).apply(instance, WandData::new)
    );
    public static final StreamCodec<FriendlyByteBuf,WandData> STREAM = StreamCodec.of(
            (buf, data) -> {
                buf.writeInt(data.allAttr.size());
                for (NumericWandAttr attr : data.allAttr) {
                    NumericWandAttr.STREAM.encode(buf, attr);
                }
                ActionCardDeck.STREAM.encode(buf, data.getDeck());
                ActionCardDeck.STREAM.encode(buf, data.getHand());
                ActionCardDeck.STREAM.encode(buf, data.getDiscard());
            },
            (buf) -> {
                int size = buf.readInt();
                List<NumericWandAttr> allAttr = new CopyOnWriteArrayList<>();
                for (int i = 0; i < size; i++) {
                    allAttr.add(NumericWandAttr.STREAM.decode(buf));
                }
                ActionCardDeck deck = ActionCardDeck.STREAM.decode(buf);
                ActionCardDeck hand = ActionCardDeck.STREAM.decode(buf);
                ActionCardDeck discard = ActionCardDeck.STREAM.decode(buf);
                return new WandData(allAttr, deck, hand, discard);
            }
    );
    private final ActionCardDeck deck;
    private final ActionCardDeck hand;
    private final ActionCardDeck discard;

    public WandData(ActionCardDeck deck, ActionCardDeck hand, ActionCardDeck discard) {
        this.deck = deck;
        this.hand = hand;
        this.discard = discard;

        // Only for debugging
        if (deck.getActions().isEmpty()) {
            this.deck.draw(new WrappedWandAction(TestAction.INSTANCE,0));
        }
    }
    public WandData(List<NumericWandAttr> allAttr, ActionCardDeck deck, ActionCardDeck hand, ActionCardDeck discard) {
        this(deck, hand, discard);
        // overwrite the default allAttr
        for (NumericWandAttr attr : allAttr) {
            for (NumericWandAttr attr2 : this.allAttr) {
                if (attr.getId().equals(attr2.getId())) {
                    attr2.setValue(attr.getDouble());
                    break;
                }
            }
        }
    }
    public WandData() {
        this(new ActionCardDeck(new ArrayList<>()), new ActionCardDeck(new ArrayList<>()), new ActionCardDeck(new ArrayList<>()));
    }
    public List<NumericWandAttr> attrList() {
        return allAttr;
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
    public void setDeck(ActionCardDeck deck) {
        this.deck.clear();
        this.deck.draw(deck);
    }
    public void setHand(ActionCardDeck hand) {
        this.hand.clear();
        this.hand.draw(hand);
    }
    public void setDiscard(ActionCardDeck discard) {
        this.discard.clear();
        this.discard.draw(discard);
    }

    public NumericWandAttr getAttr(ResourceLocation id) {
        for (NumericWandAttr attr : allAttr) {
            if (attr.getId().equals(id)) {
                return attr;
            }
        }
        return null;
    }
    @Override
    public int hashCode() {
        return allAttr.hashCode() + deck.hashCode() + hand.hashCode() + discard.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WandData other) {
            return allAttr.equals(other.allAttr) && deck.equals(other.deck) && hand.equals(other.hand) && discard.equals(other.discard);
        }
        return false;
    }
    @Override
    public String toString() {
        return "WandData{" +
                "allAttr=" + allAttr +
                ", deck=" + deck +
                ", hand=" + hand +
                ", discard=" + discard +
                '}';
    }
}

