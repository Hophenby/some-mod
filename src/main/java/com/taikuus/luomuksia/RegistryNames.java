package com.taikuus.luomuksia;

import net.minecraft.resources.ResourceLocation;

public enum RegistryNames {
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
    ACTION_MODIFIER_ACCELERATE("action_modifier_accelerate"),
    ACTION_PROJ_SPARK("action_proj_spark"),



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

    private static ResourceLocation getRL(String id) {
        return ResourceLocation.fromNamespaceAndPath(Luomuksia.MODID, id);
    }
}
