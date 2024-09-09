package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.actions.AbstractWandAction;
import com.taikuus.luomuksia.api.wand.ActionCardDeck;
import com.taikuus.luomuksia.api.wand.WandData;
import com.taikuus.luomuksia.api.wand.WrappedWandAction;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentRegistry {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Luomuksia.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WrappedWandAction>> WRAPPED_GUN_ACTION = REGISTRAR.registerComponentType(
            "action",
            builder -> builder.persistent(WrappedWandAction.CODEC).networkSynchronized(WrappedWandAction.STREAM)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ActionCardDeck>> ACTION_CARD_DECK = REGISTRAR.registerComponentType(
            "actions",
            builder -> builder.persistent(ActionCardDeck.CODEC).networkSynchronized(ActionCardDeck.STREAM)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AbstractWandAction>> ABSTRACT_WAND_ACTION = REGISTRAR.registerComponentType(
            "wand_action",
            builder -> builder.persistent(AbstractWandAction.CODEC).networkSynchronized(AbstractWandAction.STREAM)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandData>> WAND_DATA = REGISTRAR.registerComponentType(
            "wand_data",
            builder -> builder.persistent(WandData.CODEC).networkSynchronized(WandData.STREAM)
    );
}
