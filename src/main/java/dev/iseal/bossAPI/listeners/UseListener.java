package dev.iseal.bossAPI.listeners;

import dev.iseal.bossAPI.misc.abstracts.AbstractAttackClass;
import dev.iseal.bossAPI.systems.BossManager;
import dev.iseal.bossAPI.systems.attacks.AttackManager;
import dev.iseal.sealLib.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Logger;

public class UseListener implements Listener {

    private final AttackManager attackManager = AttackManager.getInstance();
    private final BossManager bossManager = BossManager.getInstance();

    private final Logger logger = Bukkit.getLogger();

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        System.out.println("use");
        if (!attackManager.isPlayerController()) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getPlayer().getEntityId() != bossManager.getBossEntity().getEntityId()) {
            return;
        }

        if (event.getItem().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("is_attack_item"), PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
            AbstractAttackClass attack = attackManager.getAttackByName(event.getItem().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("attack_name"), PersistentDataType.STRING));
            if (attack == null) {
                return;
            }
            if (attack.isTargeted()) {
                Entity hitEntity = GlobalUtils.raycastInaccurate(event.getPlayer(), attack.getDefaultRange());
                if (hitEntity == null) {
                    logger.warning("No entity hit!");
                    event.getPlayer().sendMessage("No entity hit!");
                    return;
                }
                attackManager.attemptAttack(attack, hitEntity);
            } else {
                attackManager.attemptAttack(attack, null);
            }
        }
    }
}
