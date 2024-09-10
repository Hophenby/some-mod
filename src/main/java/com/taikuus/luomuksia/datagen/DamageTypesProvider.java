package com.taikuus.luomuksia.datagen;

import com.taikuus.luomuksia.setup.MiscRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.taikuus.luomuksia.Luomuksia.MODID;

public class DamageTypesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.DAMAGE_TYPE, DamageTypesProvider::bootstrap);
    public DamageTypesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(MODID));
    }
    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(MiscRegistry.CUT_DAMAGE, new DamageType("cut",0.1f));
    }
    public static class DamageTypesTagsProvider extends DamageTypeTagsProvider {

        public DamageTypesTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pPackOutput, provider, MODID, existingFileHelper);
        }
        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider){
            tag(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(MiscRegistry.CUT_DAMAGE.location());
            tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(MiscRegistry.CUT_DAMAGE.location());
            tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).addOptional(MiscRegistry.CUT_DAMAGE.location());
        }
    }
}
