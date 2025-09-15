package dev.kai.listener;

import dev.kai.manager.DuelManager;
import dev.kai.manager.DuelManager.Duel;
import dev.kai.utility.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DuelCommandListener implements Listener {

    private final JavaPlugin plugin;
    public DuelCommandListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Duel duel = DuelManager.getInstance().getDuel(player);

        if (duel != null && duel.isActive) {
            String cmd = event.getMessage().toLowerCase();
            if (!cmd.startsWith("/draw")) {
                event.setCancelled(true);
                player.sendMessage(ColorUtil.parse("<red>You cannot use commands during a duel, except /draw!"));
            }
        }
    }
}
