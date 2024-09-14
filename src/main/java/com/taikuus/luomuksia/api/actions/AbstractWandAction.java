package com.taikuus.luomuksia.api.actions;

import com.mojang.serialization.Codec;
import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.RegistryNames;
import com.taikuus.luomuksia.api.wand.ShotStates;
import com.taikuus.luomuksia.api.wand.WandContext;
import com.taikuus.luomuksia.common.actions.EnumActionTypes;
import com.taikuus.luomuksia.common.item.WandActionItem;
import com.taikuus.luomuksia.setup.WandActionRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractWandAction {
    public static final Codec<AbstractWandAction> CODEC = ResourceLocation.CODEC.xmap(WandActionRegistry::get, AbstractWandAction::getId);
    public static final StreamCodec<FriendlyByteBuf, AbstractWandAction> STREAM = StreamCodec.of(
            (buf, action) -> buf.writeResourceLocation(action.getId()),
            (buf) -> WandActionRegistry.get(buf.readResourceLocation())
    );

    private final ResourceLocation id;
    protected int manaCost = 0;
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
        return manaCost;
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
        if (getManaCost() != 0) {
            map.put(TooltipShowableStats.MANA_COST, String.valueOf(getManaCost()));
        }
        return map;
    }
    public enum TooltipShowableStats {
        ACTION_TYPE("action_type"),
        MANA_COST("mana_cost"),
        CAST_DELAY("cast_delay"),
        RELOAD_TICKS("reload_ticks"),
        PROJECTILE_SPEED("projectile_speed"),
        PROJECTILE_INACCURACY("projectile_inaccuracy"),
        PROJECTILE_EXISTING_TIME("projectile_existing_time"),
        DAMAGE_TYPE_MOB_PROJ("damage_type_mob_proj"),
        DAMAGE_TYPE_CUTTING("damage_type_cutting"),
        DAMAGE_TYPE_EXPLOSION("damage_type_explosion"),
        DAMAGE_TYPE_FIRE("damage_type_fire"),
        DAMAGE_TYPE_ICE("damage_type_ice"),
        DAMAGE_TYPE_ELECTRIC("damage_type_electric"),
        DAMAGE_TYPE_CURSE("damage_type_curse"),
        ;
        private final String name;

        TooltipShowableStats(String name) {
            this.name = name;
        }

        public String getTranslationKey() {
            return "tooltip.action_attr." + Luomuksia.MODID + "." + name;
        }
        public ResourceLocation getIcon() {
            return RegistryNames.getRL("textures/action_attr/" + name + ".png");
        }
    }
}
