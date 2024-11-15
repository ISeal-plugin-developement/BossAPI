package dev.iseal.bossAPI;

import dev.iseal.bossAPI.listeners.DamageListener;
import dev.iseal.bossAPI.listeners.JoinListener;
import dev.iseal.bossAPI.listeners.UseListener;
import dev.iseal.sealLib.Helpers.NSKeyHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BossAPI extends JavaPlugin {

    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new UseListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);
        NSKeyHelper.getKey(this, "is_attack_item");
        NSKeyHelper.getKey(this, "attack_name");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}
