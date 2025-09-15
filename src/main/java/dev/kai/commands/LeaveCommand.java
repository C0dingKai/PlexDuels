package dev.kai.commands;

import dev.kai.manager.DuelManager;
import dev.kai.manager.DuelManager.Duel;
import dev.kai.utility.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaveCommand implements CommandExecutor {

    public LeaveCommand(org.bukkit.plugin.java.JavaPlugin plugin) {
        plugin.getCommand("leave").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        DuelManager manager = DuelManager.getInstance();
        Duel duel = manager.getDuel(player);

        if (duel == null) {
            player.sendMessage(ColorUtil.parse("<red>You are not in a duel!"));
            return true;
        }

        if (duel.isActive) {
            player.sendMessage(ColorUtil.parse("<red>You cannot leave during an active duel!"));
            return true;
        }

        manager.removeDuel(player);
        player.sendMessage(ColorUtil.parse("<gray>You left the duel."));
        Player opponent = duel.getOpponent(player);
        if (opponent != null && opponent.isOnline())
            opponent.sendMessage(ColorUtil.parse("<gray>" + player.getName() + " left the duel."));
        return true;
    }
}
