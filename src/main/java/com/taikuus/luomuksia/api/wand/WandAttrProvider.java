package com.taikuus.luomuksia.api.wand;

import com.taikuus.luomuksia.RegistryNames;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WandAttrProvider {
    private static final RandomSource RANDOM = RandomSource.create();
    /**
     * Randomizable base attributes. Usually not changeable during the game after the wand is created
     */
    private static final List<RandomizableWandAttrGenerator> RANDOMIZABLES_BASES = List.of(
            new RandomizableWandAttrGenerator(RegistryNames.WAND_MAX_MANA.get(), 50, 1500, RANDOM),
            new RandomizableWandAttrGenerator(RegistryNames.WAND_MANA_REGEN.get(), 1, 50, RANDOM),
            new RandomizableWandAttrGenerator(RegistryNames.WAND_BASIC_RELOAD_TICKS.get(), 85, -45, RANDOM),
            new RandomizableWandAttrGenerator(RegistryNames.WAND_BASIC_DELAY_TICKS.get(),  15,-15, RANDOM),
            new RandomizableWandAttrGenerator(RegistryNames.WAND_MAX_SLOTS.get(), 3, 27, RANDOM)
    );
    /**
     * Changeable attributes. Usually changes over time by the game logics
     */
    private static final List<CodecableWandAttr> DEFAULT_CHANGEABLES = List.of(

        new CodecableWandAttr(RegistryNames.WAND_MANA.get(), 50),
        new CodecableWandAttr(RegistryNames.WAND_ACCUMULATED_RELOAD_TICKS.get(), 0),
        new CodecableWandAttr(RegistryNames.WAND_REMAINING_RELOAD_TICKS.get(), 0),
        new CodecableWandAttr(RegistryNames.WAND_REMAINING_DELAY_TICKS.get(), 0)
    );


    public static class TieredAttrBuilder {
        private final int tier;
        private final List<CodecableWandAttr> list = new ArrayList<>();
        public TieredAttrBuilder(int tier) {
            this.tier = tier;
            //Luomuksia.LOGGER.debug("Creating wand wandData from tier: " + tier);
            findAndSet(RegistryNames.WAND_TIER.get(), tier);
            for (RandomizableWandAttrGenerator generator : RANDOMIZABLES_BASES) {
                list.add(generator.getIntWithTier(tier, 0, 0.1));
            }
            list.addAll(DEFAULT_CHANGEABLES);
        }
        private void findAndSet(ResourceLocation attrId, int value) {
            for (CodecableWandAttr attr : list) {
                if (attr.getId().equals(attrId)) {
                    //Luomuksia.LOGGER.info("Found and set " + attrId + " to " + value +" [tier " + tier + "]");
                    attr.setValue(value);
                    return;
                }
            }
            CodecableWandAttr newAttr = new CodecableWandAttr(attrId, value);
            list.add(newAttr);
        }
        /**
         * Determine the specific tier for one of the randomizable attributes
         * @param attrId the id of the attribute
         * @param specificTier the specific tier for the attribute
         */
        public TieredAttrBuilder determineSpecificTier(ResourceLocation attrId, int specificTier) {
            for (RandomizableWandAttrGenerator generator : RANDOMIZABLES_BASES) {
                if (generator.base.getId().equals(attrId)) {
                    findAndSet(attrId, (int) generator.getWithTier(specificTier, 0, 0.15));
                    return this;
                }
            }
            return this;
        }
        /**
         * Determine the specific value for one of the randomizable attributes
         * @param attrId the id of the attribute
         * @param specificValue the specific value of the attribute
         */
        public TieredAttrBuilder determineSpecificValue(ResourceLocation attrId, int specificValue) {
            for (RandomizableWandAttrGenerator generator : RANDOMIZABLES_BASES) {
                if (generator.base.getId().equals(attrId)) {
                    findAndSet(attrId, specificValue);
                    return this;
                }
            }
            return this;
        }

        /**
         * Overwrite all attributes with the given collection (include the base attributes)
         */
        public TieredAttrBuilder overwriteAll(Collection<CodecableWandAttr> attrs) {
            list.clear();
            list.addAll(attrs);
            return this;
        }
        public List<CodecableWandAttr> build() {
            return List.copyOf(list);
        }
    }

    private static class RandomizableWandAttrGenerator {
        private final CodecableWandAttr base;
        private final double min;
        private final double max;
        private final RandomSource random;
        private final boolean reversedGrowth;

        /**
         * Create a randomizable attribute generator
         * @param base the base attribute
         * @param min the minimum value of the attribute, if min > max, the growth is reversed
         * @param max the maximum value of the attribute, if min > max, the growth is reversed
         * @param random the random source
         */
        public RandomizableWandAttrGenerator(CodecableWandAttr base, double min, double max, RandomSource random) {
            this.base = base;
            this.min = Math.min(min, max);
            this.max = Math.max(max, min);
            this.random = random;
            reversedGrowth = min > max;
        }

        /**
         * Create a randomizable attribute generator
         * @param resourceLocation the id of the base attribute
         * @param min the minimum value of the attribute, if min > max, the growth is reversed
         * @param max the maximum value of the attribute, if min > max, the growth is reversed
         * @param random the random source
         */
        public RandomizableWandAttrGenerator(ResourceLocation resourceLocation, int min, int max, RandomSource random) {
            this(new CodecableWandAttr(resourceLocation, 0), min, max, random);
        }
        /**
         * Generate an integer with a probability
         * @param prob the probability of the attribute to be true
         */
        public CodecableWandAttr genBoolean(double prob) {
            prob = Mth.clamp(prob, 0, 1);
            return new CodecableWandAttr(base.getId(), random.nextDouble() < prob ? 1 : 0);
        }

        /**
         *
         * @return value will be ((tiered value) ± absoluteBias / 2 ± (max - min) * relativeBias / 2)
         *         using gaussian distribution may cause the value to be a little out of the range
         */
        private double getWithTier(int tier, int absoluteBias, double relativeBias) {
            tier = Mth.clamp(tier, 0, 11);
            tier = reversedGrowth ? 11 - tier : tier;
            return Mth.clamp(
                    Mth.lerp(tier / 12D, min, max) + (random.nextDouble() - 0.5D) * absoluteBias + (max - min) * relativeBias * (random.nextGaussian()),
                    min, max
            );
        }
        /**
         * Generate an integer with a tier and bias
         * @param tier the tier of the attribute within a range between 0 and 12
         * @return (the value will be ((tiered value) ± absoluteBias / 2 ± (max - min) * relativeBias / 2))
         */
        public CodecableWandAttr getIntWithTier(int tier, int absoluteBias, double relativeBias) {
            return new CodecableWandAttr(base.getId(), (int) getWithTier(tier, absoluteBias, relativeBias));
        }
        /**
         * Generate a weighted boolean with a tier and bias
         * @param tier the tier of the attribute within a range between 0 and 12
         * @return the probability to be true will be (tier * 8.33% ± relativeBias * 50%)
         *         example: tier = 3, relativeBias = 0.1, the probability will be 25% ± 5%
         */
        public CodecableWandAttr getWeightedBooleanWithTier(int tier, double relativeBias) {
            return genBoolean(Mth.clamp(getWithTier(tier, 0, relativeBias), 0, 1));
        }
    }
}
