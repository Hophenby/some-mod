package com.taikuus.luomuksia.api.actions;

import com.mojang.serialization.Codec;
import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.item.WandActionItem;
import com.taikuus.luomuksia.setup.WandActionRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractWandAction {
    private final Map<TooltipShowableStats, Number> numericShowables = Arrays.stream(TooltipShowableStats.values())
            .filter(TooltipShowableStats::isNumeric)
            .collect(LinkedHashMap::new, (map, stat) -> map.put(stat, (Number) stat.getDefaultValue().get()), LinkedHashMap::putAll);
    public static final Codec<AbstractWandAction> CODEC = ResourceLocation.CODEC.xmap(WandActionRegistry::get, AbstractWandAction::getId);
    public static final StreamCodec<FriendlyByteBuf, AbstractWandAction> STREAM = StreamCodec.of(
            (buf, action) -> buf.writeResourceLocation(action.getId()),
            (buf) -> WandActionRegistry.get(buf.readResourceLocation())
    );

    private final ResourceLocation id;
    public WandActionItem actionItem;
    private final EnumActionTypes type;

    public WandActionItem getActionItem() {
        return actionItem;
    }

    public abstract void action(WandContext context, ShotStates stats);
    public AbstractWandAction(ResourceLocation id, EnumActionTypes type) {
        this.id = id;
        this.type = type;
    }
    public ResourceLocation getId() {
        return id;
    }
    public int getManaCost() {
        return getNumericShowable(TooltipShowableStats.MANA_COST).intValue();
    }
    public int getCastDelay() {
        return getNumericShowable(TooltipShowableStats.CAST_DELAY).intValue();
    }
    public int getReloadTicks() {
        return getNumericShowable(TooltipShowableStats.RELOAD_TICKS).intValue();
    }
    public double getInaccuracy() {
        return getNumericShowable(TooltipShowableStats.PROJECTILE_INACCURACY).doubleValue();
    }
    public Number getNumericShowable(TooltipShowableStats stat) {
        return numericShowables.get(stat);
    }
    public void setNumericShowable(TooltipShowableStats stat, Number value) {
        numericShowables.put(stat, value);
    }
    public void setNumericShowables(Map<TooltipShowableStats, Number> showables) {
        numericShowables.putAll(showables);
    }
    public void addDelayAndReload(WandContext context) {
        context.addDelayTicks(getCastDelay());
        context.addReloadTicks(getReloadTicks());
    }
    public EnumActionTypes getType() {
        return type;
    }
    public Component getDescription() {
        return Component.translatable("tooltip.action." + getId().toLanguageKey() + ".desc");
    }

    /**
     * Returns a map of tooltip showables for this action. Ordered by the order of the enum.
     * @return a map of tooltip showables for this action.
     */
    public Map<TooltipShowableStats, String> getTooltipShowables() {
        //return manaCost > 0 ?
        //        Map.of(TooltipShowableStats.MANA_COST, String.valueOf(manaCost),TooltipShowableStats.ACTION_TYPE, type.get()) :
        //        Map.of(TooltipShowableStats.ACTION_TYPE, type.get());
        Map<TooltipShowableStats,String> map = new LinkedHashMap<>();
        String translatedType = getType().translatable().getString();
        map.put(TooltipShowableStats.ACTION_TYPE, translatedType);
        numericShowables.forEach((stat, value) -> {
            if (!Objects.equals(stat.defaultValue.get(), value)) map.put(stat, String.valueOf(value));
        });
        return map;
    }
    public enum TooltipShowableStats {
        ACTION_TYPE("action_type", () -> EnumActionTypes.OTHER),
        MANA_COST("mana_cost"),
        CAST_DELAY("cast_delay"),
        RELOAD_TICKS("reload_ticks"),
        PROJECTILE_SPEED("projectile_speed", () -> 0.0F),
        PROJECTILE_INACCURACY("projectile_inaccuracy", () -> 0.0D),
        PROJECTILE_EXISTING_TIME("projectile_existing_time"),
        DAMAGE_TYPE_MOB_PROJ("damage_type_mob_proj", () -> 0.0F),
        DAMAGE_TYPE_CUTTING("damage_type_cutting", () -> 0.0F),
        DAMAGE_TYPE_EXPLOSION("damage_type_explosion", () -> 0.0F),
        DAMAGE_TYPE_FIRE("damage_type_fire", () -> 0.0F),
        DAMAGE_TYPE_ICE("damage_type_ice", () -> 0.0F),
        DAMAGE_TYPE_ELECTRIC("damage_type_electric", () -> 0.0F),
        DAMAGE_TYPE_CURSE("damage_type_curse", () -> 0.0F),
        EXPLOSION_LEVEL("explosion_level", () -> 0),
        ;
        private final String name;
        private final Supplier<?> defaultValue;
        private final boolean isNumeric;

        TooltipShowableStats(String name) {
            this.name = name;
            this.defaultValue = () -> 0;
            this.isNumeric = true;
        }
        TooltipShowableStats(String name, Supplier<?> defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.isNumeric = defaultValue.get() instanceof Number;
        }

        public String getTranslationKey() {
            return "tooltip.action_attr." + Luomuksia.MODID + "." + name;
        }
        public ResourceLocation getIcon() {
            return RegistryNames.getRL("textures/action_attr/" + name + ".png");
        }
        public Supplier<?> getDefaultValue() {
            return defaultValue;
        }
        public boolean isNumeric() {
            return isNumeric;
        }
    }
}
