package com.taikuus.luomuksia.api.wand.wandattr;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.setup.WandAttrRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WandAttrProvider {
    private static final List<Holder<WandAttr>> baseAttrs = List.of(
            WandAttrRegistry.ATTR_BASIC_DELAY_TICKS,
            WandAttrRegistry.ATTR_BASIC_RELOAD_TICKS,
            WandAttrRegistry.ATTR_MAX_SLOTS,
            WandAttrRegistry.ATTR_MAX_MANA,
            WandAttrRegistry.ATTR_MANA_REGEN
    );
    /**
     * Randomizable base attributes. Usually not changeable during the game after the wand is created
     */
    public static Map<Holder<WandAttr>, WandAttrInstance> getBaseAttrs() {
        TieredAttrBuilder builder = TieredAttrBuilder.create().add(WandAttrRegistry.ATTR_TIER);
        for (Holder<WandAttr> attr : baseAttrs) {
            builder.add(attr);
        }
        return builder.build();
    }
    public static TieredAttrBuilder setupAttrs(int tier) {
        RandomSource rdSrc = RandomSource.create();
        double bias = 0.3;
        List<Double> rawValues = baseAttrs.stream().filter(attr -> attr.value() instanceof RangedWandAttr)
                .map(attr -> rdSrc.nextDouble() * bias * 2 + 1 - bias)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        Luomuksia.LOGGER.info("Raw values: " + rawValues);
        //Normalize the values
        double sum = rawValues.stream().mapToDouble(Double::doubleValue).sum();
        return baseAttrs.stream()
                .filter(attr -> attr.value() instanceof RangedWandAttr)
                .collect(() -> TieredAttrBuilder.tier(tier),
                        (b, attr) -> {
                            AtomicInteger i = new AtomicInteger();
                            double value = rawValues.get(i.getAndIncrement());
                            b.add(attr, ((RangedWandAttr) attr.value()).getLinearIntpValue(Mth.clamp((float) (value * tier / sum / 3.12f), 0f, 1f)));
                        },
                        TieredAttrBuilder::add);

    }
    /**
     * Changeable attributes. Usually changes over time by the game logics
     */
    public static Map<Holder<WandAttr>, WandAttrInstance.Mutable> getMutableAttrs() {
        return TieredAttrBuilder.create()
                .add(WandAttrRegistry.ATTR_ACCUMULATED_RELOAD_TICKS)
                .add(WandAttrRegistry.ATTR_REMAINING_DELAY_TICKS)
                .add(WandAttrRegistry.ATTR_REMAINING_RELOAD_TICKS)
                .add(WandAttrRegistry.ATTR_LAST_DELAY_TICKS)
                .add(WandAttrRegistry.ATTR_LAST_RELOAD_TICKS)
                .add(WandAttrRegistry.ATTR_MANA)
                .buildMutable();
    }


    public static class TieredAttrBuilder {
        private final Map<Holder<WandAttr>, WandAttrInstance.Mutable> map = new Object2ObjectOpenHashMap<>();
        private TieredAttrBuilder(int tier) {
            add(WandAttrRegistry.ATTR_TIER, tier);
        }
        private TieredAttrBuilder() {
        }
        public static TieredAttrBuilder tier(int tier) {
            return new TieredAttrBuilder(tier);
        }
        public static TieredAttrBuilder create() {
            return new TieredAttrBuilder();
        }
        public TieredAttrBuilder add(Holder<WandAttr> attr, double value) {
            map.put(attr, new WandAttrInstance.Mutable(attr,
                    attr.value().sanitizeValue(value)));
            return this;
        }
        public TieredAttrBuilder addNoSanitize(Holder<WandAttr> attr, double value) {
            map.put(attr, new WandAttrInstance.Mutable(attr, value));
            return this;
        }
        public TieredAttrBuilder add(Holder<WandAttr> attr) {
            return add(attr, attr.value().getBaseValue());
        }
        private TieredAttrBuilder add(TieredAttrBuilder builder) {
            map.putAll(builder.map);
            return this;
        }
        public Map<Holder<WandAttr>, WandAttrInstance> build() {
            Map<Holder<WandAttr>, WandAttrInstance> result = new Object2ObjectOpenHashMap<>();
            map.forEach((attr, mutable) -> result.put(attr, mutable.toImmutable()));
            return result;
        }
        public Map<Holder<WandAttr>, WandAttrInstance.Mutable> buildMutable() {
            return new Object2ObjectOpenHashMap<>(map);
        }
    }


}
