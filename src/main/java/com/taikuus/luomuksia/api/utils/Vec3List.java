package com.taikuus.luomuksia.api.utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
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
    public PairedIterartor getPairedIterator(){
        return new PairedIterartor();
    }

    /**
     * Returns an iterator that iterates over the list in pairs of every two points.
     * The first point is the start of the current pair and the end of last pair.
     */
    public class PairedIterartor implements Iterator<Vec3Pair>{
        private final Iterator<Vec3> iterator;
        private Vec3 tail = null;
        private PairedIterartor(){
            iterator = Vec3List.this.iterator();
        }
        @Override
        public boolean hasNext() {
            return Vec3List.this.size() >= 2 && iterator.hasNext();
        }

        @Override
        public Vec3Pair next() {
            if (tail == null){
                Vec3 head = iterator.next();
                tail = iterator.next();
                return new Vec3Pair(head, tail);
            }
            Vec3 head = tail;
            tail = iterator.next();
            return new Vec3Pair(head, tail);
        }
    }

    public record Vec3Pair(Vec3 head, Vec3 tail) {
    }
}
