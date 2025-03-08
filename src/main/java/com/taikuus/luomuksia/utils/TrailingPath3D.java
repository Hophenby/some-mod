package com.taikuus.luomuksia.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A list of 3D points.
 * It is used to represent a path in 3D space.
 */
public class TrailingPath3D extends LinkedList<TrailingPath3D.Vec3WithTime> {
    public static final StreamCodec<RegistryFriendlyByteBuf, TrailingPath3D> STREAM_CODEC = StreamCodec.of(
            TrailingPath3D::encode,
            TrailingPath3D::decode
    );
    public static void encode(RegistryFriendlyByteBuf buf, TrailingPath3D list){
        buf.writeInt(list.size());
        for (Vec3WithTime vec : list){
            Vec3WithTime.STREAM_CODEC.encode(buf, vec);
        }
    }
    public static TrailingPath3D decode(RegistryFriendlyByteBuf buf){
        TrailingPath3D list = new TrailingPath3D();
        int size = buf.readInt();
        for (int i = 0; i < size; i++){
            list.add(Vec3WithTime.STREAM_CODEC.decode(buf));
        }
        return list;
    }
    public CompoundTag toNBT(){
        CompoundTag tag = new CompoundTag();
        int i = 0;
        for (Vec3WithTime vec_t : this){
            tag.putInt("time" + i, vec_t.time);
            tag.putDouble("x" + i, vec_t.vec().x);
            tag.putDouble("y" + i, vec_t.vec().y);
            tag.putDouble("z" + i, vec_t.vec().z);
            i++;
        }
        return tag;
    }
    public static TrailingPath3D fromNBT(CompoundTag tag){
        TrailingPath3D list = new TrailingPath3D();
        int i = 0;
        while (tag.contains("time" + i)){
            int time = tag.getInt("time" + i);
            double x = tag.getDouble("x" + i);
            double y = tag.getDouble("y" + i);
            double z = tag.getDouble("z" + i);
            list.add(new Vec3WithTime(new Vec3(x, y, z), time));
            i++;
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trail3D[");
        for (Vec3WithTime vec_t : this){
            builder.append(vec_t).append(", ");
        }
        builder.append("]");
        return builder.toString();
    }

    public PairedIterator getPairedIterator(){
        return new PairedIterator();
    }
    public PairedIterator getPairedIterator(int afterWhen){
        return new PairedIterator(afterWhen);
    }
    public boolean collideWith(AABB aabb, int afterWhen){
        PairedIterator it = getPairedIterator(afterWhen);
        while (it.hasNext()){
            Vec3TPair pair = it.next();
            Vec3 head = pair.head.vec;
            Vec3 tail = pair.tail.vec;
            if (aabb.intersects(head.x, head.y, head.z, tail.x, tail.y, tail.z)){
                return true;
            }
        }
        return false;
    }
    public boolean collideWith(AABB aabb){
        return collideWith(aabb, 0);
    }
    @Nullable
    public AABB getBoundingBoxAfterWhen(int afterWhen){
        List<Vec3> filtered = this.stream()
                .filter(vec_t -> vec_t.time >= afterWhen)
                .map(vec_t -> vec_t.vec)
                .toList();
        if (filtered.size() == 0){
            return null;
        }
        AABB aabb = new AABB(filtered.get(0), filtered.get(0));
        for (Vec3 vec : filtered){
            aabb = aabb.minmax(new AABB(vec, vec));
        }
        return aabb;
    }

    public record Vec3WithTime(Vec3 vec, int time) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Vec3WithTime> STREAM_CODEC = StreamCodec.of(
                (buf, vec_t) -> {
                    buf.writeDouble(vec_t.vec.x);
                    buf.writeDouble(vec_t.vec.y);
                    buf.writeDouble(vec_t.vec.z);
                    buf.writeInt(vec_t.time);
                },
                (buf) -> new Vec3WithTime(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()), buf.readInt())
        );

        @Override
        public String toString() {
            return "Vec3WithTime{" +
                    "vec=" + vec +
                    ", time=" + time +
                    '}';
        }
    }
    public record Vec3TPair(Vec3WithTime head, Vec3WithTime tail) {
    }

    /**
     * Returns an iterator that iterates over the list in pairs of every two points.
     * The first point is the start of the current pair and the end of last pair.
     */
    public class PairedIterator implements Iterator<Vec3TPair>{
        private final Iterator<Vec3WithTime> iterator;
        private Vec3WithTime tail = null;
        private PairedIterator(){
            iterator = TrailingPath3D.this.iterator();
        }
        private PairedIterator(int afterWhen){
            iterator = TrailingPath3D.this.iterator();
            while (iterator.hasNext()){
                Vec3WithTime vec_t = iterator.next();
                if (vec_t.time >= afterWhen){
                    tail = vec_t;
                    break;
                }
            }
        }
        @Override
        public boolean hasNext() {
            return TrailingPath3D.this.size() >= 2 && iterator.hasNext();
        }

        @Override
        public Vec3TPair next() {
            if (tail == null){
                Vec3WithTime head = iterator.next();
                tail = iterator.next();
                return new Vec3TPair(head, tail);
            }
            Vec3WithTime head = tail;
            tail = iterator.next();
            return new Vec3TPair(head, tail);
        }
    }

}
