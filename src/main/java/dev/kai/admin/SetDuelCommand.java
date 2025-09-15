package dev.kai.admin;

import dev.kai.manager.DuelManager;
import dev.kai.utility.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SetDuelCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public SetDuelCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("setduel").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if(!player.hasPermission("plexduels.admin")){
            player.sendMessage("You do not have permission to use this command!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /setduel <1|2>");
            return true;
        }

        switch (args[0]) {
            case "1" -> {
                DuelManager.getInstance().setDuelLocation1(player.getLocation());
                player.sendMessage(ColorUtil.parse("<green>Duel spawn 1 set!"));
            }
            case "2" -> {
                DuelManager.getInstance().setDuelLocation2(player.getLocation());
                player.sendMessage(ColorUtil.parse("<green>Duel spawn 2 set!"));
            }
        }

        return true;
    }
}
