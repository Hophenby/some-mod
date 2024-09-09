package com.taikuus.luomuksia.event;

import com.taikuus.luomuksia.Luomuksia;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Luomuksia.MODID)
public class CastEvents {
    @SubscribeEvent
    public static void castDiscount(SpellCostCalcEvent event){
        var caster = event.context.getUnwrappedCaster();
        SpellContext contextClone = event.context.clone();
    }
}
