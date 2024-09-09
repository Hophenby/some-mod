package com.example.examplemod.api.wand;

import com.example.examplemod.RegistryNames;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WandData {
    public final List<CodecableWandAttr> allAttr = List.of(
            new CodecableWandAttr(RegistryNames.WAND_MAX_MANA.get(), 100),
            new CodecableWandAttr(RegistryNames.WAND_MANA_REGEN.get(), 1),
            new CodecableWandAttr(RegistryNames.WAND_BASIC_RELOAD_TICKS.get(), 20),
            new CodecableWandAttr(RegistryNames.WAND_BASIC_DELAY_TICKS.get(), 10),
            new CodecableWandAttr(RegistryNames.WAND_MAX_SLOTS.get(), 5),

            new CodecableWandAttr(RegistryNames.WAND_MANA.get(), 100),
            new CodecableWandAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get(), 0),
            new CodecableWandAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get(), 0),
            new CodecableWandAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get(), 0)
    );
    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(CodecableWandAttr.CODEC).fieldOf("allAttr").forGetter(WandData::attrList),
                    ActionCardDeck.CODEC.fieldOf("deck").forGetter(WandData::getDeck),
                    ActionCardDeck.CODEC.fieldOf("hand").forGetter(WandData::getHand),
                    ActionCardDeck.CODEC.fieldOf("discard").forGetter(WandData::getDiscard)
            ).apply(instance, WandData::new)
    );
    public static final StreamCodec<FriendlyByteBuf,WandData> STREAM = StreamCodec.of(
            (buf, data) -> {
                buf.writeInt(data.allAttr.size());
                for (CodecableWandAttr attr : data.allAttr) {
                    CodecableWandAttr.STREAM.encode(buf, attr);
                }
                ActionCardDeck.STREAM.encode(buf, data.getDeck());
                ActionCardDeck.STREAM.encode(buf, data.getHand());
                ActionCardDeck.STREAM.encode(buf, data.getDiscard());
            },
            (buf) -> {
                int size = buf.readInt();
                List<CodecableWandAttr> allAttr = new CopyOnWriteArrayList<>();
                for (int i = 0; i < size; i++) {
                    allAttr.add(CodecableWandAttr.STREAM.decode(buf));
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
    }
    public WandData(List<CodecableWandAttr> allAttr, ActionCardDeck deck, ActionCardDeck hand, ActionCardDeck discard) {
        this(deck, hand, discard);
        // overwrite the default allAttr
        for (CodecableWandAttr attr : allAttr) {
            for (CodecableWandAttr attr2 : this.allAttr) {
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
    public WandData copy() {
        return new WandData(allAttr, deck.copy(), hand.copy(), discard.copy());
    }
    public List<CodecableWandAttr> attrList() {
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
    public ActionCardDeck getAllActions() {
        ActionCardDeck all = new ActionCardDeck(new ArrayList<>());
        all.draw(deck);
        all.draw(hand);
        all.draw(discard);
        all.orderDeck();
        return all;
    }
    public CodecableWandAttr getAttr(ResourceLocation id) {
        for (CodecableWandAttr attr : allAttr) {
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

    public void tick() {
        CodecableWandAttr manaRegen = getAttr(RegistryNames.WAND_MANA_REGEN.get());
        getAttr(RegistryNames.WAND_MANA.get()).setValue(
                Math.min(getAttr(RegistryNames.WAND_MANA.get()).getDoubleAsInt() + manaRegen.getDoubleAsInt(),
                        getAttr(RegistryNames.WAND_MAX_MANA.get()).getDoubleAsInt())
        );
        getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).setValue(
                Math.max(getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).getDoubleAsInt() - 1,
                        0)
        );
        getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).setValue(
                Math.max(getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).getDoubleAsInt() - 1,
                        0)
        );

    }
}

