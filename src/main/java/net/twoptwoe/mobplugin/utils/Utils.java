/**
 * Utils.java
 * <p>
 * Created on 10:18:38
 */
package net.twoptwoe.mobplugin.utils;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static final Server SERVER = Server.getInstance();

    public static final void logServerInfo(String text) {
        SERVER.getLogger().info(TextFormat.GOLD + "[MobPlugin] " + text);
    }

    /**
     * Returns a random number between min (inkl.) and max (excl.) If you want a number between 1 and 4 (inkl) you need to call rand (1, 5)
     *
     * @param min min inklusive value
     * @param max max exclusive value
     * @return
     */
    public static int rand(int min, int max) {
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Returns random boolean
     *
     * @return a boolean random value either <code>true</code> or <code>false</code>
     */
    public static boolean rand() {
        return ThreadLocalRandom.current().nextBoolean();
    }

}
