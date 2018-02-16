package net.daporkchop.mcpe;

import java.util.Random;

public class UtilsPE {
    private static final Random random = new Random(System.currentTimeMillis());

    public static final int random(int max) {
        return random.nextInt(max);
    }

    public static final int mRound(int value, int factor) {
        return Math.round(value / factor) * factor;
    }
}
