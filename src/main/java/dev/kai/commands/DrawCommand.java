package dev.kai.commands;

import dev.kai.manager.DuelManager;
import dev.kai.manager.DuelManager.Duel;
import dev.kai.utility.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DrawCommand implements CommandExecutor {

    public DrawCommand(org.bukkit.plugin.java.JavaPlugin plugin) {
        plugin.getCommand("draw").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        DuelManager manager = DuelManager.getInstance();
        Duel duel = manager.getDuel(player);

        if (duel == null || !duel.isActive) {
            player.sendMessage(ColorUtil.parse("<red>No active duel to draw."));
            return true;
        }

        if (player.equals(duel.player1)) duel.player1Draw = true;
        if (player.equals(duel.player2)) duel.player2Draw = true;

        Player opponent = duel.getOpponent(player);

        if (duel.player1Draw && duel.player2Draw) {
            duel.isActive = false;
            duel.startGracePeriod();
            duel.player1.sendMessage(ColorUtil.parse("<gray>The duel ended in a draw!"));
            duel.player2.sendMessage(ColorUtil.parse("<gray>The duel ended in a draw!"));
        } else {
            player.sendMessage(ColorUtil.parse("<gray>Waiting for your opponent to draw..."));
            if (opponent != null && opponent.isOnline())
                opponent.sendMessage(ColorUtil.parse("<#278EF5>" + player.getName() + " <gray>has requested a draw. Type /draw to accept."));


        }
        return true;
    }
}
