package dev.iseal.bossAPI.listeners;

import dev.iseal.bossAPI.events.BossDamageEvent;
import dev.iseal.bossAPI.systems.BossManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        BossManager bossManager = BossManager.getInstance();

        if (!bossManager.isBoss(entity)) {
            return;
        }
        bossManager.damageBoss(event.getDamage());
        Bukkit.getPluginManager().callEvent(new BossDamageEvent(event.getDamage(), event.getEntity(), bossManager.getBossEntity()));
        event.setDamage(0);
    }

}
