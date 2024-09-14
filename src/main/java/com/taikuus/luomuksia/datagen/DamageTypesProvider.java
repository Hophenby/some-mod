package com.taikuus.luomuksia.datagen;

import com.taikuus.luomuksia.setup.MiscRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
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
        ctx.register(MiscRegistry.DamageTypeRegistry.CUT_DAMAGE.getKey(), new DamageType("wand_damage_cutting",0.1f));
        ctx.register(MiscRegistry.DamageTypeRegistry.PROJ_DAMAGE.getKey(), new DamageType("wand_damage_proj",0.1f));
    }
    public static class DamageTypesTagsProvider extends DamageTypeTagsProvider {

        public DamageTypesTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pPackOutput, provider, MODID, existingFileHelper);
        }
        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider){
            ResourceLocation cutId = MiscRegistry.DamageTypeRegistry.CUT_DAMAGE.getId();
            tag(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(cutId);
            tag(DamageTypeTags.BYPASSES_ARMOR).addOptional(cutId);
            tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).addOptional(cutId);

            ResourceLocation projId = MiscRegistry.DamageTypeRegistry.PROJ_DAMAGE.getId();
            tag(DamageTypeTags.BYPASSES_COOLDOWN).addOptional(projId);
            tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS).addOptional(projId);
        }
    }
}
