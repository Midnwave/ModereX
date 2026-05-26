package com.blockforge.moderex.disguise.packet;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Centralizes Unsafe access and provides a record-safe objectFieldOffset helper.
 *
 * Unsafe.objectFieldOffset(Field) throws UnsupportedOperationException on record
 * components (Java 16+).  The two-argument overload objectFieldOffset(Class, String)
 * works on records but is not part of the sun.misc.Unsafe compile-time API in all
 * JDK distributions, so we invoke it via reflection as a fallback.
 */
final class UnsafeHelper {

    static final Unsafe UNSAFE;

    // objectFieldOffset(Class<?>, String) — added in JDK 16, not always visible at compile time
    private static final Method OFFSET_BY_NAME;

    static {
        Unsafe u = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            u = (Unsafe) f.get(null);
        } catch (Exception ignored) {}
        UNSAFE = u;

        Method m = null;
        if (u != null) {
            try {
                m = Unsafe.class.getMethod("objectFieldOffset", Class.class, String.class);
            } catch (NoSuchMethodException ignored) {}
        }
        OFFSET_BY_NAME = m;
    }

    private UnsafeHelper() {}

    /**
     * Returns the field offset. Handles record components by falling back to the
     * two-argument overload via reflection when the single-Field overload throws.
     * Returns -1 if the offset cannot be determined.
     */
    static long offset(Field f) {
        if (UNSAFE == null) return -1L;
        try {
            return UNSAFE.objectFieldOffset(f);
        } catch (UnsupportedOperationException e) {
            // Record component — use the 2-arg overload (JDK 16+) via reflection
            if (OFFSET_BY_NAME != null) {
                try {
                    return (long) OFFSET_BY_NAME.invoke(UNSAFE, f.getDeclaringClass(), f.getName());
                } catch (Exception ex) {
                    return -1L;
                }
            }
            return -1L;
        }
    }
}
