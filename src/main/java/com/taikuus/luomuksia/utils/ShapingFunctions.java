package com.taikuus.luomuksia.utils;

import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;

public class ShapingFunctions {
    /**
     * Returns a vector that is the result of rotating the given vector by 2 * PI / n * i
     * @param i the index of the side
     * @param n the number of sides of the polygon
     * @param vec the vector to rotate
     * @return the rotated vector
     */
    public static Vec3 normalPolygonH(int i, int n, Vec3 vec) {
        return vec.yRot((float) (i * 2 * Math.PI / n));
    }
    public static TriFunction<Integer, Integer, Vec3, Vec3> fixedIntervalAngleH(Double angle) {
        return (i, n, vec) -> vec.yRot((float) (-angle * (n - 1) / 2 + angle * i));
    }
}
