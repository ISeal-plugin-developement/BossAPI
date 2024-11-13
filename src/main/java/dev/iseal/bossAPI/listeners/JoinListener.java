package dev.iseal.bossAPI.listeners;

import dev.iseal.bossAPI.systems.BossManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static dev.iseal.sealLib.SealLib.isDebug;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {

        if (isDebug())
            return;
        if (BossManager.getInstance().isFighting())
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "A boss fight is currently in progress. Please wait for it to finish.");
    }
}
