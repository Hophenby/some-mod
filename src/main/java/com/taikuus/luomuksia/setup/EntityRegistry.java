package com.taikuus.luomuksia.setup;

import com.taikuus.luomuksia.Luomuksia;
import com.taikuus.luomuksia.api.utils.Vec3List;
import com.taikuus.luomuksia.common.entity.fx.FadeLightFxProj;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileBouncingBall;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileLightBlade;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileSpark;
import com.taikuus.luomuksia.common.entity.projectile.ProjectileStoneCutter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.taikuus.luomuksia.Luomuksia.MODID;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTRAR = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    // FX
    public static final DeferredHolder<EntityType<?>, EntityType<FadeLightFxProj>> FADE_LIGHT = ENTITY_REGISTRAR.register(
            "fade_light",
            () -> EntityType.Builder.<FadeLightFxProj>of(FadeLightFxProj::new, MobCategory.MISC)
                    .sized(0F, 0F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .fireImmune()
                    .noSave()
                    .build("fade_light")
    );
    // Projectiles
    /**
     * net.minecraft.world.entity.MobCategory#MISC
     */
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileSpark>> PROJECTILE_SPARK = ENTITY_REGISTRAR.register(
            "projectile_spark",
            () -> EntityType.Builder.<ProjectileSpark>of(ProjectileSpark::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(2)
                    .noSave()
                    .build("projectile_spark")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileBouncingBall>> PROJECTILE_BOUNCING_BALL = ENTITY_REGISTRAR.register(
            "projectile_bouncing_ball",
            () -> EntityType.Builder.<ProjectileBouncingBall>of(ProjectileBouncingBall::new, MobCategory.MISC)
                    .sized(0.2F, 0.2F)
                    .clientTrackingRange(4)
                    .updateInterval(2)
                    .noSave()
                    .build("projectile_bouncing_ball")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileStoneCutter>> PROJECTILE_STONE_CUTTER = ENTITY_REGISTRAR.register(
            "projectile_stone_cutter",
            () -> EntityType.Builder.<ProjectileStoneCutter>of(ProjectileStoneCutter::new, MobCategory.MISC)
                    .sized(0.5625F,1.0F )
                    .clientTrackingRange(4)
                    .updateInterval(2)
                    .noSave()
                    .build("projectile_stone_cutter")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ProjectileLightBlade>> PROJECTILE_LIGHT_BLADE = ENTITY_REGISTRAR.register(
            "projectile_light_blade",
            () -> EntityType.Builder.<ProjectileLightBlade>of(ProjectileLightBlade::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(4)
                    .updateInterval(2)
                    .noSave()
                    .build("projectile_light_blade")
    );
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZER_REGISTRAR = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Luomuksia.MODID);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Vec3List>> LINKED_VEC3_LIST = DATA_SERIALIZER_REGISTRAR.register(
            "linked_vec3_list", () -> EntityDataSerializer.forValueType(Vec3List.STREAM_CODEC));
}
