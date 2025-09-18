package dev.kai.listener;

import dev.kai.manager.DuelManager;
import dev.kai.manager.DuelManager.Duel;
import dev.kai.utility.ColorUtil;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Sound;
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
           winner.showTitle(Title.title(
                   ColorUtil.parse("<#00FC22>You won!"),
                   ColorUtil.empty()
           ));
        }
        dead.playSound(dead.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
        dead.showTitle(Title.title(
                ColorUtil.parse("<#FC2A00>You lost"),
                ColorUtil.empty()
        ));
        dead.setGameMode(GameMode.SPECTATOR);
    }
}
