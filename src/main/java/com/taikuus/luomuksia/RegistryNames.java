package com.taikuus.luomuksia;

import net.minecraft.resources.ResourceLocation;

public enum RegistryNames {
    WAND_TIER("wand_tier"),
    WAND_MAX_MANA("wand_max_mana"),
    WAND_MANA_REGEN("wand_mana_regen"),
    WAND_BASIC_RELOAD_TICKS("wand_basic_reload_ticks"),
    WAND_BASIC_DELAY_TICKS("wand_basic_delay_ticks"),
    WAND_MAX_SLOTS("wand_max_action_cards"),
    WAND_MANA("wand_mana"),
    WAND_REMAINING_RELOAD_TICKS("wand_remaining_reload_ticks"),
    WAND_ACCUMULATED_RELOAD_TICKS("wand_accumulated_reload_ticks"),
    WAND_REMAINING_DELAY_TICKS("wand_remaining_delay_ticks"),
    WAND_ACTION_CARD_DECK("wand_action_card_deck"),
    WAND_ACTION_CARD_HAND("wand_action_card_hand"),
    WAND_ACTION_CARD_DISCARD("wand_action_card_discard"),

    ACTION_TEST("action_test"),
    ACTION_ADD_TRIGGER("action_add_trigger"),
    ACTION_MODIFIER_ACCELERATE("action_modifier_accelerate"),
    ACTION_MODIFIER_HOMING("action_modifier_homing"),
    ACTION_MODIFIER_EXPLOSIVE_HIT("action_modifier_explosive_hit"),
    ACTION_MODIFIER_LIGHT("action_modifier_light"),
    ACTION_MODIFIER_REDIRECTING_HOMING("action_modifier_redirecting_homing"),
    ACTION_SCATTERED_MULTICAST("action_scattered_multicast"),
    ACTION_PROJ_SPARK("action_proj_spark"),
    ACTION_PROJ_SC("action_proj_stonecutter"),
    ACTION_PROJ_BOUNCING("action_proj_bouncing_ball"),

    DAMAGE_TYPE_CUT("damage_type_cut"),
    DAMAGE_TYPE_PROJ("damage_type_proj"),


    ;
    private final ResourceLocation id;

    RegistryNames(String path) {
        this.id = getRL(path);
    }
    public ResourceLocation get() {
        return id;
    }
    public String getPath() {
        return id.getPath();
    }

    public static ResourceLocation getRL(String path) {
        return ResourceLocation.fromNamespaceAndPath(Luomuksia.MODID, path);
    }
}
