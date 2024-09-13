package com.taikuus.luomuksia.api.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.taikuus.luomuksia.RegistryNames;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class WandData {
    public final List<CodecableWandAttr> allAttr = new WandAttrProvider.TieredAttrBuilder(1).build();
    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(CodecableWandAttr.CODEC).fieldOf("allAttr").forGetter(WandData::attrList),
                    ActionCardDeck.CODEC.fieldOf("deck").forGetter(WandData::getDeck),
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
                ActionCardDeck.STREAM.encode(buf, data.getDiscard());
            },
            (buf) -> {
                int size = buf.readInt();
                List<CodecableWandAttr> allAttr = new CopyOnWriteArrayList<>();
                for (int i = 0; i < size; i++) {
                    allAttr.add(CodecableWandAttr.STREAM.decode(buf));
                }
                ActionCardDeck deck = ActionCardDeck.STREAM.decode(buf);
                ActionCardDeck discard = ActionCardDeck.STREAM.decode(buf);
                return new WandData(allAttr, deck, discard);
            }
    );
    private final ActionCardDeck deck;
    private final ActionCardDeck discard;

    public WandData(ActionCardDeck deck, ActionCardDeck discard) {
        this.deck = deck;
        this.discard = discard;
    }
    public WandData(List<CodecableWandAttr> allAttr, ActionCardDeck deck, ActionCardDeck discard) {
        this(deck, discard);
        // overwrite the default allAttr
        this.overwriteAllAttr(allAttr);
    }
    public void overwriteAllAttr(List<CodecableWandAttr> allAttr) {
        for (CodecableWandAttr attr : allAttr) {
            for (CodecableWandAttr attr2 : this.allAttr) {
                if (attr.getId().equals(attr2.getId())) {
                    attr2.setValue(attr.getValue());
                    break;
                }
            }
        }
    }
    public WandData() {
        this(new ActionCardDeck(new ArrayList<>()), new ActionCardDeck(new ArrayList<>()));
    }
    public static WandData fromTier(int tier) {
        //Luomuksia.LOGGER.debug("Creating wand wandData from tier: " + tier);
        return new WandData(new WandAttrProvider.TieredAttrBuilder(tier).build(), new ActionCardDeck(new ArrayList<>()), new ActionCardDeck(new ArrayList<>()));
    }
    public static WandData custom(WandAttrProvider.TieredAttrBuilder builder) {
        return new WandData(builder.build(), new ActionCardDeck(new ArrayList<>()), new ActionCardDeck(new ArrayList<>()));
    }
    public WandData copy() {
        return new WandData(allAttr, deck.copy(), discard.copy());
    }
    public List<CodecableWandAttr> attrList() {
        return allAttr;
    }
    public ActionCardDeck getDeck() {
        return deck;
    }
    public ActionCardDeck getDiscard() {
        return discard;
    }
    public void setDeck(ActionCardDeck deck) {
        this.deck.clear();
        this.deck.draw(deck);
    }
    public void setDiscard(ActionCardDeck discard) {
        this.discard.clear();
        this.discard.draw(discard);
    }
    public ActionCardDeck getAllActions() {
        ActionCardDeck all = new ActionCardDeck(new ArrayList<>());
        all.draw(deck);
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
        return allAttr.hashCode() + deck.hashCode() + discard.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WandData other) {
            return allAttr.equals(other.allAttr) && deck.equals(other.deck) && discard.equals(other.discard);
        }
        return false;
    }
    @Override
    public String toString() {
        return "WandData{" +
                "allAttr=" + allAttr +
                ", deck=" + deck +
                ", discard=" + discard +
                '}';
    }
    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();
        for (CodecableWandAttr attr : allAttr) {
            tooltip.add(attr.getTooltip());
        }
        return tooltip;
    }
    public List<Component> getTooltip(List<ResourceLocation> filter) {
        List<Component> tooltip = new ArrayList<>();
        for (CodecableWandAttr attr : allAttr) {
            if (filter.contains(attr.getId())) {
                tooltip.add(attr.getTooltip());
            }
        }
        return tooltip;
    }

    public void tick() {
        CodecableWandAttr manaRegen = getAttr(RegistryNames.WAND_MANA_REGEN.get());
        getAttr(RegistryNames.WAND_MANA.get()).setValue(
                Math.min(getAttr(RegistryNames.WAND_MANA.get()).getValue() + manaRegen.getValue(),
                        getAttr(RegistryNames.WAND_MAX_MANA.get()).getValue())
        );
        getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).setValue(
                Math.max(getAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get()).getValue() - 1, 0)
        );
        getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).setValue(
                Math.max(getAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get()).getValue() - 1, 0)
        );

    }
}

