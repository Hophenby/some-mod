package com.example.examplemod.setup;

import com.example.examplemod.common.entity.projectile.ProjectileSpark;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.example.examplemod.ExampleMod.MODID;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> REGISTRAR = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileSpark>> PROJECTILE_SPARK = REGISTRAR.register(
            "projectile_spark",
            () -> EntityType.Builder.<ProjectileSpark>of(ProjectileSpark::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .noSave()
                    .build("projectile_spark")
    );

}
