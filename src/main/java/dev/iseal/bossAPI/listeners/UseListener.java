package dev.iseal.bossAPI.listeners;

import dev.iseal.bossAPI.misc.abstracts.AbstractAttackClass;
import dev.iseal.bossAPI.systems.BossManager;
import dev.iseal.bossAPI.systems.attacks.AttackManager;
import dev.iseal.sealLib.Helpers.NSKeyHelper;
import dev.iseal.sealLib.Utils.SpigotGlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.logging.Logger;

public class UseListener implements Listener {

    private final AttackManager attackManager = AttackManager.getInstance();
    private final BossManager bossManager = BossManager.getInstance();

    private final Logger logger = Bukkit.getLogger();

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!attackManager.isPlayerController()) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        if (event.getPlayer().getEntityId() != bossManager.getBossEntity().getEntityId()) {
            return;
        }

        ItemMeta itemMeta = event.getItem().getItemMeta();
        if (itemMeta.getPersistentDataContainer().has(NSKeyHelper.getKey("is_attack_item"), PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);

            AbstractAttackClass attack = attackManager.getAttackByName(itemMeta.getPersistentDataContainer().get(NSKeyHelper.getKey("attack_name"), PersistentDataType.STRING));
            if (attack == null) {
                return;
            }

            if (attack.isTargeted()) {
                Entity hitEntity = SpigotGlobalUtils.raycastInaccurate(event.getPlayer(), attack.getRange());
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
