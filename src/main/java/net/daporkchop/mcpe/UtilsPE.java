package net.daporkchop.mcpe;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.scheduler.Task;
import gnu.trove.impl.sync.TSynchronizedIntSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class UtilsPE {
    public static final TIntSet BANNED_BLOCKS = new TIntHashSet();

    public static final int SERVER_SHUTDOWN_TIME_SECONDS = (int) TimeUnit.HOURS.toSeconds(6L);
    private static final int SHUTDOWN_TICKS = SERVER_SHUTDOWN_TIME_SECONDS * 20;

    static {
        BANNED_BLOCKS.addAll(new int[] {
                BlockID.BEDROCK,
                BlockID.DRAGON_EGG,
                BlockID.END_PORTAL,
                BlockID.END_PORTAL_FRAME,
                BlockID.INVISIBLE_BEDROCK
        });
    }

    public static int random(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    public static int mRound(int value, int factor) {
        return Math.round(value / factor) * factor;
    }

    public static void init(final Server s) {
        /*s.getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                s.dispatchCommand(new ConsoleCommandSender(), "gc");
            }
        }, 6000, 6000);*/
        s.getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                Server.getInstance().getNetwork().setName(MultiMOTD.getMOTD());
            }
        }, 2);
        s.getScheduler().scheduleDelayedRepeatingTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                Server.getInstance().getOnlinePlayers().forEach((uuid, player) -> {
                    if (player.level.getDimension() == Level.DIMENSION_NETHER && player.getY() > 127.5d) {
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
        {
            Map<Integer, String> notificationTimes = new HashMap<Integer, String>()    {
                {
                    this.register(TimeUnit.MINUTES, 10L);
                    this.register(TimeUnit.MINUTES, 5L);
                    this.register(TimeUnit.MINUTES, 2L);
                    this.register(TimeUnit.MINUTES, 1L, "minute");
                    this.register(TimeUnit.SECONDS, 30L);
                    this.register(TimeUnit.SECONDS, 15L);
                    this.register(TimeUnit.SECONDS, 10L);
                    this.register(TimeUnit.SECONDS, 5L);
                    this.register(TimeUnit.SECONDS, 4L);
                    this.register(TimeUnit.SECONDS, 3L);
                    this.register(TimeUnit.SECONDS, 2L);
                    this.register(TimeUnit.SECONDS, 1L, "second");
                    this.register(TimeUnit.SECONDS, 0L);
                }

                protected void register(TimeUnit unit, long amount) {
                    this.register(unit, amount, unit.name().toLowerCase());
                }

                protected void register(TimeUnit unit, long amount, String unitName) {
                    int time = (int) unit.toSeconds(amount);
                    this.put(SHUTDOWN_TICKS - time * 20, amount == 0L ? "" : String.format("§c§lServer restarting in %d %s...", amount, unitName));
                }
            };
            notificationTimes.forEach((time, msg) -> {
                s.getScheduler().scheduleDelayedTask(new Task() {
                    @Override
                    public void onRun(int currentTick) {
                        if (msg.isEmpty())  {
                            stopNow();
                        } else {
                            Server.getInstance().broadcastMessage(msg);
                        }
                    }
                }, time);
            });
        }
    }

    public static void stripBannedBlocks(FullChunk chunk)   {
        for (int x = 15; x >= 0; x--)   {
            for (int z = 15; z >= 0; z--)   {
                for (int y = 255; y > 0; y--)  {
                    if (BANNED_BLOCKS.contains(chunk.getBlockId(x, y, z)))  {
                        chunk.setBlockId(x, y, z, BlockID.AIR);
                    }
                }
                chunk.setBlockId(x, 0, z, BlockID.BEDROCK);
            }
        }

        if (chunk.getProvider().getLevel().getDimension() == Level.DIMENSION_NETHER)    {
            for (int x = 15; x >= 0; x--)   {
                for (int z = 15; z >= 0; z--)   {
                    chunk.setBlockId(x, 127, z, BlockID.BEDROCK);
                }
            }
        }
    }

    /**
     * Stops the server now, disconnecting all players and whatnot
     */
    @SuppressWarnings("deprecation")
    public static void stopNow() {
        Server.getInstance().getScheduler().scheduleTask(() -> {
            Server s = Server.getInstance();
            s.dispatchCommand(new ConsoleCommandSender(), "save-all");
            s.getOnlinePlayers().values().forEach(p -> p.kick("§9§lServer restarting...", false));
            s.dispatchCommand(new ConsoleCommandSender(), "stop");
        });
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
        return min + ThreadLocalRandom.current().nextInt(max - min);
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
