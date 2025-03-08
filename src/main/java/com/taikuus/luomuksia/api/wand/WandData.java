package com.taikuus.luomuksia.api.wand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.taikuus.luomuksia.api.wand.wandattr.WandAttr;
import com.taikuus.luomuksia.api.wand.wandattr.WandAttrInstance;
import com.taikuus.luomuksia.api.wand.wandattr.WandAttrProvider;
import com.taikuus.luomuksia.setup.WandAttrRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WandData {
    public Map<Holder<WandAttr>, WandAttrInstance> getAttributes() {
        return attributes;
    }

    public Map<Holder<WandAttr>, WandAttrInstance.Mutable> getAllMutable() {
        return allMutable;
    }

    public final Map<Holder<WandAttr>, WandAttrInstance> attributes = WandAttrProvider.getBaseAttrs();
    public final Map<Holder<WandAttr>, WandAttrInstance.Mutable> allMutable = WandAttrProvider.getMutableAttrs();
    public static final Codec<WandData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.simpleMap(WandAttr.CODEC, WandAttrInstance.CODEC, WandAttrRegistry.WAND_ATTR_REGISTRY).fieldOf("attributes").forGetter(WandData::getAttributes),
                    Codec.simpleMap(WandAttr.CODEC, WandAttrInstance.MUTABLE_CODEC, WandAttrRegistry.WAND_ATTR_REGISTRY).fieldOf("mutables").forGetter(WandData::getAllMutable),
                    ActionCardDeck.CODEC.fieldOf("deck").forGetter(WandData::getDeck),
                    ActionCardDeck.CODEC.fieldOf("discard").forGetter(WandData::getDiscard)
            ).apply(instance, WandData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WandData> STREAM = StreamCodec.of(
            (buf, data) -> {
                buf.writeInt(data.attributes.size());
                data.attributes.forEach((attr, instance) -> {
                    WandAttr.STREAM.encode(buf, attr);
                    WandAttrInstance.STREAM.encode(buf, instance);
                });
                buf.writeInt(data.allMutable.size());
                data.allMutable.forEach((attr, instance) -> {
                    WandAttr.STREAM.encode(buf, attr);
                    WandAttrInstance.MUTABLE_STREAM.encode(buf, instance);
                });
                ActionCardDeck.STREAM.encode(buf, data.getDeck());
                ActionCardDeck.STREAM.encode(buf, data.getDiscard());
            },
            (buf) -> {
                int size = buf.readInt();
                Map<Holder<WandAttr>, WandAttrInstance> attr = new Object2ObjectOpenHashMap<>();
                for (int i = 0; i < size; i++) {
                    Holder<WandAttr> holder = WandAttr.STREAM.decode(buf);
                    WandAttrInstance instance = WandAttrInstance.STREAM.decode(buf);
                    attr.put(holder, instance);
                }
                size = buf.readInt();
                Map<Holder<WandAttr>, WandAttrInstance.Mutable> attr1 = new Object2ObjectOpenHashMap<>();
                for (int i = 0; i < size; i++) {
                    Holder<WandAttr> holder = WandAttr.STREAM.decode(buf);
                    WandAttrInstance.Mutable instance = WandAttrInstance.MUTABLE_STREAM.decode(buf);
                    attr1.put(holder, instance);
                }
                ActionCardDeck deck = ActionCardDeck.STREAM.decode(buf);
                ActionCardDeck discard = ActionCardDeck.STREAM.decode(buf);
                return new WandData(attr, attr1, deck, discard);
            }
    );
    private final ActionCardDeck deck;
    private final ActionCardDeck discard;

    public WandData(ActionCardDeck deck, ActionCardDeck discard) {
        this.deck = deck;
        this.discard = discard;
    }
    public WandData() {
        this(ActionCardDeck.empty(), ActionCardDeck.empty());
    }
    public WandData(Map<Holder<WandAttr>, WandAttrInstance> attributes, ActionCardDeck deck, ActionCardDeck discard) {
        this(deck, discard);
        // overwrite the default allAttr
        this.attributes.clear();
        this.attributes.putAll(attributes);
    }
    public WandData(Map<Holder<WandAttr>, WandAttrInstance> attributes, Map<Holder<WandAttr>, WandAttrInstance.Mutable> mutables, ActionCardDeck deck, ActionCardDeck discard) {
        this(attributes, deck, discard);
        this.allMutable.clear();
        mutables.forEach((attr, mutable) -> this.allMutable.put(attr, mutable.copy()));
    }
    public static WandData fromTier(int tier) {
        //Luomuksia.LOGGER.debug("Creating wand wandData from tier: " + tier);
        return new WandData(WandAttrProvider.setupAttrs(tier).build(), ActionCardDeck.empty(), ActionCardDeck.empty());
    }
    public static WandData custom(WandAttrProvider.TieredAttrBuilder builder) {
        return new WandData(builder.build(), ActionCardDeck.empty(), ActionCardDeck.empty());
    }
    public WandData copy() {
        return new WandData(attributes, allMutable, deck.copy(), discard.copy());
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
        ActionCardDeck all = ActionCardDeck.empty();
        all.draw(deck);
        all.draw(discard);
        all.orderDeck();
        return all;
    }
    public WandAttrInstance getAttr(Holder<WandAttr> attr) {
        return attributes.get(attr);
    }
    public int getWandSize() {
        return (int) getAttr(WandAttrRegistry.ATTR_MAX_SLOTS).value();
    }
    public int getMana() {
        return (int) getMutable(WandAttrRegistry.ATTR_MANA).getValue();
    }
    public int getMaxMana() {
        return (int) getAttr(WandAttrRegistry.ATTR_MAX_MANA).value();
    }
    public WandAttrInstance.Mutable getMutable(Holder<WandAttr> attr) {
        return allMutable.get(attr);
    }
    @Override
    public int hashCode() {
        return attributes.hashCode() + deck.hashCode() + discard.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WandData other) {
            return attributes.equals(other.attributes) && deck.equals(other.deck) && discard.equals(other.discard);
        }
        return false;
    }
    @Override
    public String toString() {
        return "WandData{" +
                "allAttr=" + attributes +
                ", allMutable=" + allMutable +
                ", deck=" + deck +
                ", discard=" + discard +
                '}';
    }
    public List<Component> getTooltip() {
        List<Component> tooltip = new ArrayList<>();
        attributes.forEach((attr, instance) -> tooltip.add(instance.getTooltip()));
        return tooltip;
    }
    public List<Component> getTooltip(List<Holder<WandAttr>> filter) {
        List<Component> tooltip = new ArrayList<>();
        filter.forEach(attr -> {
            if (attributes.containsKey(attr)) {
                tooltip.add(attributes.get(attr).getTooltip());
            }
        });
        return tooltip;
    }
    private void restoreMana() {
        if (getMaxMana() <= getMana()) return;
        double manaRegen = getAttr(WandAttrRegistry.ATTR_MANA_REGEN).value();
        //restore mana
        getMutable(WandAttrRegistry.ATTR_MANA).setValue(
                Math.min(getMana() + manaRegen, getMaxMana()));
        //Luomuksia.LOGGER.info("Restoring mana: " + manaRegen + " Remaining mana: " + getMana());
    }
    private void tickCooldowns() {
        getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).setValue(
                Math.max(getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).getValue() - 1, 0)
        );
        getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).setValue(
                Math.max(getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).getValue() - 1, 0)
        );
        if (getMutable(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS).getValue() == 0) {
            getMutable(WandAttrRegistry.ATTR_LAST_RELOAD_TICKS).setValue(0);
        }
        if (getMutable(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS).getValue() == 0) {
            getMutable(WandAttrRegistry.ATTR_LAST_DELAY_TICKS).setValue(0);
        }
    }

    public void tickData() {
        //restore mana
        restoreMana();
        //Luomuksia.LOGGER.debug("Restoring mana: " + manaRegen.getValue() + " Remaining mana: " + getAttr(RegistryNames.WAND_MANA.get()).getValue());

        //reload and delay
        tickCooldowns();

    }
}

