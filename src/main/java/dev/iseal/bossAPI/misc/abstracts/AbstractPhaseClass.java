package dev.iseal.bossAPI.misc.abstracts;

import dev.iseal.bossAPI.BossAPI;
import dev.iseal.bossAPI.tasks.LoopSongTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;

public abstract class AbstractPhaseClass {

    private final String name;
    private final double minHealth;
    private final double maxHealth;
    private final double damageMultiplier;
    private final double speedMultiplier;
    private final double rangeMultiplier;
    private final double healthRegen;
    private final long regenSpeed;
    private final BarColor bossBarColor;
    private final boolean doesRegen;
    private final boolean hasSong;
    private final boolean isSongLethargic;
    private boolean hasSongStarted;
    private final long songDurationTicks;
    private final String songNamespace;
    private final String songName;
    private final boolean canFly;
    private final boolean resetOnEnd;

    private LoopSongTask songTask;

    public AbstractPhaseClass(String name, double minHealth, double maxHealth, double damageMultiplier, double speedMultiplier, double rangeMultiplier, double healthRegen, long regenSpeed, boolean doesRegenerate, BarColor bossBarColor, boolean canFly, boolean resetOnEnd) {
        this.name = name;
        this.minHealth = minHealth;
        this.maxHealth = maxHealth;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.rangeMultiplier = rangeMultiplier;
        this.bossBarColor = bossBarColor;
        this.doesRegen = doesRegenerate;
        this.healthRegen = healthRegen;
        this.regenSpeed = regenSpeed;
        this.hasSong = false;
        this.isSongLethargic = false;
        this.songDurationTicks = 0;
        this.hasSongStarted = false;
        this.songNamespace = null;
        this.songName = null;
        this.canFly = canFly;
        this.resetOnEnd = resetOnEnd;
    }

    /**
    * @param name the name of the phase
    * @param isSongLethargic if the song is lethargic, meaning you have to manually start it. not really reccomanded except in special cases
     */
    public AbstractPhaseClass(String name, double minHealth, double maxHealth, double damageMultiplier, double speedMultiplier, double rangeMultiplier, double healthRegen, long regenSpeed, boolean doesRegenerate, BarColor bossBarColor, String songNamespace, String songName, boolean isSongLethargic, long songDurationTicks, boolean canFly, boolean resetOnEnd) {
        this.name = name;
        this.minHealth = minHealth;
        this.maxHealth = maxHealth;
        this.damageMultiplier = damageMultiplier;
        this.speedMultiplier = speedMultiplier;
        this.rangeMultiplier = rangeMultiplier;
        this.bossBarColor = bossBarColor;
        this.doesRegen = doesRegenerate;
        this.healthRegen = healthRegen;
        this.regenSpeed = regenSpeed;
        this.hasSong = true;
        this.hasSongStarted = false;
        this.isSongLethargic = isSongLethargic;
        this.songDurationTicks = songDurationTicks;
        this.songNamespace = songNamespace;
        this.songName = songName;
        this.canFly = canFly;
        this.resetOnEnd = resetOnEnd;
    }

    public String getPhaseName() {
        return name;
    }

    public boolean isInRange(double health) {
        return health >= minHealth && health <= maxHealth;
    }

    public double getActualDamage(double damage) {
        return damage * damageMultiplier;
    }

    public double getActualSpeed(double speed) {
        return speed * speedMultiplier;
    }

    public double getActualRange(double range) {
        return range * rangeMultiplier;
    }

    public boolean doesRegenerate() {
        return doesRegen;
    }

    public long getRegenSpeed() {
        return regenSpeed;
    }

    public double getNewHealth(double health) {
        return health + healthRegen;
    }

    public BarColor getBossBarColor() {
        return bossBarColor;
    }

    public boolean hasSong() {
        return hasSong;
    }

    /**
        * Checks if the song is lethargic
     * <p>
     * <p>
     * being lethargic means that the song has to be manually started by the plugin making the phase.
     * <p>
     * basically, it's not my problem.
     *
        * @return true if the song is lethargic
     */
    public boolean isSongLethargic() {
        return isSongLethargic;
    }

    public boolean hasSongStarted() {
        return hasSongStarted;
    }

    public boolean canFly() {
        return canFly;
    }

    /*
        * Starts a song on loop
        * @param location the location where the song will be played, kinda useless
        *
        * location is useless cause song reach is infinite. just need a location for the world. (0,0,0) is fine
     */
    public void startSong(Location location) {
        if (!hasSong || !isSongLethargic || hasSongStarted) {
            return;
        }

        LoopSongTask task = new LoopSongTask(songNamespace, songName, location);
        task.runTaskTimer(BossAPI.getPlugin(), 0, songDurationTicks);
        songTask = task;
        hasSongStarted = true;
    }

    public void stopSong() {
        if (!hasSong || !hasSongStarted) {
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.stopSound(songNamespace + ":" + songName));
        if (songTask != null) {
            songTask.cancel();
            songTask = null;
        }
        hasSongStarted = false;
    }

    public boolean isResetOnEndPhase() {
        return resetOnEnd;
    }

    public double getMinHealth() {
        return minHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }
}
