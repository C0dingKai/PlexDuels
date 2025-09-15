package dev.kai.listener;

import dev.kai.manager.DuelManager;
import dev.kai.manager.DuelManager.Duel;
import dev.kai.utility.ColorUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DuelListener implements Listener {

    private final JavaPlugin plugin;
    public DuelListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        DuelManager manager = DuelManager.getInstance();

        if (!manager.isInDuel(dead)) return;

        Duel duel = manager.getDuel(dead);
        if (duel == null) return;

        Player winner = duel.getOpponent(dead);

        duel.winner = winner;
        duel.loser = dead;
        duel.isActive = false;

        duel.startGracePeriod();

        if (winner != null && winner.isOnline()) {
            winner.sendMessage(ColorUtil.parse("<green>You won the duel against " + dead.getName() + "!"));
        }
        dead.sendMessage(ColorUtil.parse("<red>You lost the duel against " + winner.getName() + "!"));
        dead.setGameMode(GameMode.SPECTATOR);
    }
}
