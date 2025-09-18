package dev.kai.commands;

import dev.kai.manager.DuelManager;
import dev.kai.manager.DuelManager.Duel;
import dev.kai.utility.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class LeaveCommand implements CommandExecutor {

    public LeaveCommand(org.bukkit.plugin.java.JavaPlugin plugin) {
        plugin.getCommand("leave").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
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



        Player opponent = duel.getOpponent(player);
        manager.leaveDuel(player);

        player.sendMessage(ColorUtil.parse("<gray>You left the duel."));

        return true;
    }
}
