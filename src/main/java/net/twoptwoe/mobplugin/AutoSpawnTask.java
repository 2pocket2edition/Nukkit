package net.twoptwoe.mobplugin;

public class AutoSpawnTask implements Runnable {
    @Override
    public void run() {
        MobPlugin.spawnMobs();
    }
}
