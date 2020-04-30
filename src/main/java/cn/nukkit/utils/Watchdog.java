package cn.nukkit.utils;

import cn.nukkit.Server;
import net.daporkchop.mcpe.discord.DiscordMain;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.function.Consumer;

public class Watchdog extends Thread {
    private static final boolean ENABLE = !Boolean.parseBoolean(System.getProperty("2p2e.nowatchdog", "false"));

    private final Server server;
    private final long time;
    public boolean running;
    private boolean responding = true;

    public Watchdog(Server server, long time) {
        this.server = server;
        this.time = time;
        this.running = true;
        this.setName("Watchdog");
        this.setDaemon(true);
    }

    public void kill() {
        running = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void run() {
        while (ENABLE && this.running && server.isRunning()) {
            long current = server.getNextTick();
            if (current != 0) {
                long diff = System.currentTimeMillis() - current;
                if (diff > time) {
                    if (responding) {
                        responding = false;
                        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(String.format("stall_%d.txt", System.currentTimeMillis()))))) {
                            Consumer<String> logger = s -> {
                                    this.server.getLogger().emergency(s);
                                    try {
                                        os.write(String.format("%s\n", s).getBytes("UTF-8"));
                                    } catch (UnsupportedOperationException | IOException e) {
                                        throw new RuntimeException(e);
                                    }
                            };
                            logger.accept("--------- Server stopped responding --------- (" + (diff / 1000d) + "s)");
                            logger.accept("Please report this to nukkit:");
                            logger.accept(" - https://github.com/NukkitX/Nukkit/issues/new");
                            logger.accept("---------------- Main thread ----------------");

                            dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(this.server.getPrimaryThread().getId(), Integer.MAX_VALUE), logger);

                            logger.accept("---------------- All threads ----------------");
                            ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                            for (int i = 0; i < threads.length; i++) {
                                if (i != 0)
                                    logger.accept("------------------------------");
                                dumpThread(threads[i], logger);
                            }
                            logger.accept("---------------------------------------------");
                            DiscordMain.shutdown(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            Runtime.getRuntime().exit(999);
                        }
                    }
                } else {
                    responding = true;
                }
            }
            try {
                synchronized (this) {
                    this.wait(Math.max(time / 4, 1000));
                }
            } catch (InterruptedException ignore) {}
        }
    }

    private static void dumpThread(ThreadInfo thread, Consumer<String> logger) {
        logger.accept("Current Thread: " + thread.getThreadName());
        logger.accept("\tPID: " + thread.getThreadId() + " | Suspended: " + thread.isSuspended() + " | Native: " + thread.isInNative() + " | State: " + thread.getThreadState());
        // Monitors
        if (thread.getLockedMonitors().length != 0) {
            logger.accept("\tThread is waiting on monitor(s):");
            for (MonitorInfo monitor : thread.getLockedMonitors()) {
                logger.accept("\t\tLocked on:" + monitor.getLockedStackFrame());
            }
        }

        logger.accept("\tStack:");
        for (StackTraceElement stack : thread.getStackTrace()) {
            logger.accept("\t\t" + stack);
        }
    }
}
