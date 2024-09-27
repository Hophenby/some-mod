package com.taikuus.luomuksia.api.utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;

public class Vec3List extends LinkedList<Vec3> {
    public static final StreamCodec<FriendlyByteBuf, Vec3List> STREAM_CODEC = StreamCodec.of(
            Vec3List::encode,
            Vec3List::decode
    );
    public static void encode(FriendlyByteBuf buf, Vec3List list){
        buf.writeInt(list.size());
        for (Vec3 vec : list){
            buf.writeDouble(vec.x);
            buf.writeDouble(vec.y);
            buf.writeDouble(vec.z);
        }
    }
    public static Vec3List decode(FriendlyByteBuf buf){
        Vec3List list = new Vec3List();
        int size = buf.readInt();
        for (int i = 0; i < size; i++){
            list.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
        return list;
    }
}
