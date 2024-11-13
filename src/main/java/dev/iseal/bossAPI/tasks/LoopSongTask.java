package dev.iseal.bossAPI.tasks;

import dev.iseal.sealLib.Utils.SoundHelper;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class LoopSongTask extends BukkitRunnable {

    private final String namespace;
    private final String sound;
    private final Location startLoc;

    public LoopSongTask(String namespace, String sound, Location startLoc) {
        this.namespace = namespace;
        this.sound = sound;
        this.startLoc = startLoc;
    }

    @Override
    public void run() {
        SoundHelper.playSoundInWorld(namespace, sound, 1, startLoc);
    }
}
