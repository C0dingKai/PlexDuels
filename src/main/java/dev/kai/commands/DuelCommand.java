package dev.kai.commands;

import dev.kai.manager.DuelManager;
import dev.kai.utility.ColorUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DuelCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final Random random = new Random();
    private final DuelManager duelManager;

    public DuelCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.duelManager = DuelManager.getInstance();
        plugin.getCommand("duel").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ColorUtil.parse("<red>Usage: /duel <player|accept|deny>"));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "accept" -> handleAccept(player);
            case "deny" -> handleDeny(player);
            default -> handleRequest(player, sub);
        }
        return true;
    }

    private void handleRequest(Player challenger, String targetName) {
        Player target = plugin.getServer().getPlayerExact(targetName);
        if (target == null || !target.isOnline()) {
            challenger.sendMessage(ColorUtil.parse("<red>" + targetName + " is not online."));
            challenger.playSound(challenger.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        if (target.equals(challenger)) {
            challenger.sendMessage(ColorUtil.parse("<red>You cannot duel yourself."));
            challenger.playSound(challenger.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        duelManager.addRequest(target.getUniqueId(), challenger.getUniqueId());

        challenger.sendMessage(ColorUtil.parse("<#FC2A00><bold>DUELS <gray><!bold>➡ <white>You've sent a duel request to <red>" + target.getName()));
        target.sendMessage(ColorUtil.parse("<#FC2A00><bold>DUELS <gray><!bold>➡ <white>You've received a duel request from <red>" + challenger.getName()));
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
    }

    private void handleAccept(Player target) {
        UUID challengerId = duelManager.getRequest(target.getUniqueId());
        if (challengerId == null) {
            target.sendMessage(ColorUtil.parse("<red>You have no pending duel requests."));
            return;
        }

        Player challenger = plugin.getServer().getPlayer(challengerId);
        if (challenger == null || !challenger.isOnline()) {
            target.sendMessage(ColorUtil.parse("<red>The challenger is no longer online."));
            duelManager.removeRequest(target.getUniqueId());
            return;
        }

        duelManager.removeRequest(target.getUniqueId());

        challenger.sendMessage(ColorUtil.parse("<green>" + target.getName() + " accepted your duel!"));
        target.sendMessage(ColorUtil.parse("<green>You accepted the duel request from " + challenger.getName() + "!"));

        duelManager.startDuel(challenger, target);
    }

    private void handleDeny(Player target) {
        UUID challengerId = duelManager.getRequest(target.getUniqueId());
        if (challengerId == null) {
            target.sendMessage(ColorUtil.parse("<red>You have no pending duel requests."));
            return;
        }

        Player challenger = plugin.getServer().getPlayer(challengerId);
        if (challenger != null && challenger.isOnline()) {
            challenger.sendMessage(ColorUtil.parse("<red>" + target.getName() + " denied your duel request."));
        }

        target.sendMessage(ColorUtil.parse("<gray>You denied the duel request."));
        duelManager.removeRequest(target.getUniqueId());
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String current = args[0].toLowerCase();

            if ("accept".startsWith(current)) completions.add("accept");
            if ("deny".startsWith(current)) completions.add("deny");

            if (sender instanceof Player playerSender) {
                for (Player online : plugin.getServer().getOnlinePlayers()) {
                    if (!online.equals(playerSender) && online.getName().toLowerCase().startsWith(current)) {
                        completions.add(online.getName());
                    }
                }
            }
        }

        return completions;
    }
}