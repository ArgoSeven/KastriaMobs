package org.argoseven.kastriamobs.client;

import java.util.Random;

public final class KastriaColorHelper {
    private static final Random RANDOM = new Random();
    private static final float INV_255 = 1f / 255f;

    private final int r;
    private final int g;
    private final int b;

    public KastriaColorHelper(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
    public static KastriaColorHelper randomColor() {
        return new KastriaColorHelper(
                RANDOM.nextInt(256),
                RANDOM.nextInt(256),
                RANDOM.nextInt(256)
        );
    }

    public static KastriaColorHelper fromHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        int value = Integer.parseInt(hex, 16);
        int r = (value >> 16) & 0xFF;
        int g = (value >> 8)  & 0xFF;
        int b = value & 0xFF;
        return new KastriaColorHelper(r, g, b);
    }
    public float[] toFloatColor() {
        return new float[]{
                r * INV_255,
                g * INV_255,
                b * INV_255
        };
    }

    public int[] toIntArray() {
        return new int[]{r, g, b};
    }

    public int r() { return r; }
    public int g() { return g; }
    public int b() { return b; }
}


