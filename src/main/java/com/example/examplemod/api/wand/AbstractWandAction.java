package com.example.examplemod.api.wand;

import com.example.examplemod.setup.GunActionRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractWandAction {
    public static final Codec<AbstractWandAction> CODEC = ResourceLocation.CODEC.xmap(GunActionRegistry::get, AbstractWandAction::getId);
    public static final StreamCodec<FriendlyByteBuf, AbstractWandAction> STREAM = StreamCodec.of(
            (buf, action) -> buf.writeResourceLocation(action.getId()),
            (buf) -> GunActionRegistry.get(buf.readResourceLocation())
    );

    private final ResourceLocation id;
    public final String name;
    public String desc;
    protected int manaCost = 0;
    public abstract void action(WandContext context, ShotState stats);
    public AbstractWandAction(ResourceLocation id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }
    public ResourceLocation getId() {
        return id;
    }
    public int getManaCost() {
        return manaCost;
    }
}