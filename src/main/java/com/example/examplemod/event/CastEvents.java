package com.example.examplemod.event;

import com.example.examplemod.ExampleMod;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class CastEvents {
    @SubscribeEvent
    public static void castDiscount(SpellCostCalcEvent event){
        var caster = event.context.getUnwrappedCaster();
        SpellContext contextClone = event.context.clone();
    }
}
