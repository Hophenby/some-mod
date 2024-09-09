package com.taikuus.luomuksia;

import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;

import java.util.ArrayList;
import java.util.List;

public class ArsRegistry {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>(); //this will come handy for datagen
    public static void setup(){
        //register all the spells
    }
    public static void register(AbstractSpellPart spellPart){
    GlyphRegistry.registerSpell(spellPart);
    registeredSpells.add(spellPart);
}
}
