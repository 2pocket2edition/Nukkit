package net.daporkchop.mcpe;

import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.scheduler.Task;

import java.util.Random;

public class UtilsPE {
    private static final Random random = new Random(System.currentTimeMillis());

    public static final int random(int max) {
        return random.nextInt(max);
    }

    public static final int mRound(int value, int factor) {
        return Math.round(value / factor) * factor;
    }

    public static final void init(final Server s) {
        s.getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                s.dispatchCommand(new ConsoleCommandSender(), "gc");
            }
        }, 3600, 3600);
    }
}
