package net.daporkchop.mcpe;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Level;
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
        }, 6000, 6000);
        s.getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                Server.getInstance().getNetwork().setName(MultiMOTD.getMOTD());
            }
        }, 250);
        s.getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
                    if (player.level.getDimension() == Level.DIMENSION_NETHER && player.getY() > 127.5d)    {
                        EntityDamageEvent ev = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, Integer.MAX_VALUE);
                        player.getServer().getPluginManager().callEvent(ev);
                        if (!ev.isCancelled()) {
                            player.setLastDamageCause(ev);
                            player.setHealth(0);
                        }
                    }
                });
            }
        }, 40, 40);
        s.getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                s.getOnlinePlayers().values().forEach(p -> p.kick("Server restarting..."));
                s.dispatchCommand(new ConsoleCommandSender(), "stop");
            }
        }, 432000);
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
            return max;
        }
        return min + random.nextInt(max - min);
    }

    /**
     * Returns random boolean
     *
     * @return a boolean random value either <code>true</code> or <code>false</code>
     */
    public static boolean rand() {
        return random.nextBoolean();
    }
}
