package dev.kai.manager;

import dev.kai.PlexDuels;
import dev.kai.utility.LocationUtil;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelManager {

    private static DuelManager instance;

    public static DuelManager getInstance() {
        if (instance == null) instance = new DuelManager();
        return instance;
    }

    private final Map<UUID, UUID> duelRequests = new HashMap<>();
    private final Map<UUID, Duel> activeDuels = new HashMap<>();
    private Location duelLocation1;
    private Location duelLocation2;

    private DuelManager() {}

    public void addRequest(UUID target, UUID challenger) { duelRequests.put(target, challenger); }
    public UUID getRequest(UUID target) { return duelRequests.get(target); }
    public void removeRequest(UUID target) { duelRequests.remove(target); }

    public void setDuelLocation1(Location loc) { duelLocation1 = loc; }
    public void setDuelLocation2(Location loc) { duelLocation2 = loc; }
    public Location getDuelLocation1() { return duelLocation1; }
    public Location getDuelLocation2() { return duelLocation2; }

    public void load(FileConfiguration config) {
        Location loc1 = LocationUtil.loadLocation(config, "duel.pos1");
        Location loc2 = LocationUtil.loadLocation(config, "duel.pos2");
        if (loc1 != null) duelLocation1 = loc1;
        if (loc2 != null) duelLocation2 = loc2;
    }

    public void save(FileConfiguration config) {
        if (duelLocation1 != null) LocationUtil.saveLocation(config, "duel.pos1", duelLocation1);
        if (duelLocation2 != null) LocationUtil.saveLocation(config, "duel.pos2", duelLocation2);
    }

    public void startDuel(Player p1, Player p2) {
        if (duelLocation1 == null || duelLocation2 == null) return;
        Location old1 = p1.getLocation();
        Location old2 = p2.getLocation();

        if (Math.random() < 0.5) {
            p1.teleport(duelLocation1);
            p2.teleport(duelLocation2);
        } else {
            p1.teleport(duelLocation2);
            p2.teleport(duelLocation1);
        }

        Duel duel = new Duel(p1, p2, old1, old2);
        activeDuels.put(p1.getUniqueId(), duel);
        activeDuels.put(p2.getUniqueId(), duel);
    }

    public boolean isInDuel(Player player) { return activeDuels.containsKey(player.getUniqueId()); }
    public Duel getDuel(Player player) { return activeDuels.get(player.getUniqueId()); }

    public void removeDuel(Player player) {
        Duel duel = activeDuels.get(player.getUniqueId());
        if (duel != null) {
            duel.player1.teleport(duel.pos1);
            duel.player2.teleport(duel.pos2);
            activeDuels.remove(duel.player1.getUniqueId());
            activeDuels.remove(duel.player2.getUniqueId());
        }
    }
    public void leaveDuel(Player player) {
        Duel duel = activeDuels.get(player.getUniqueId());
        if (duel == null) return;

        if (player.isOnline()) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SURVIVAL);
            }
            player.teleport(duel.pos1.equals(player.getLocation()) ? duel.pos1 : duel.pos2);
            player.stopSound(Sound.MUSIC_DISC_RELIC);
        }

        activeDuels.remove(player.getUniqueId());
        if (duel.player1 != null && duel.player1.equals(player)) duel.player1 = null;
        if (duel.player2 != null && duel.player2.equals(player)) duel.player2 = null;

        if ((duel.player1 == null || !duel.player1.isOnline()) &&
                (duel.player2 == null || !duel.player2.isOnline())) {
            if (duel.graceTask != null) {
                duel.graceTask.cancel();
                duel.graceTask = null;
            }
        }
    }



    public static class Duel {
        public Player player1, player2;
        public Location pos1, pos2;
        public boolean isActive = true;
        public Player winner, loser;
        public boolean player1Draw = false, player2Draw = false;
        private BukkitRunnable graceTask;

        public Duel(Player p1, Player p2, Location old1, Location old2) {
            this.player1 = p1;
            this.player2 = p2;
            this.pos1 = old1;
            this.pos2 = old2;
        }

        public Player getOpponent(Player p) { return p.equals(player1) ? player2 : player1; }

        public void startGracePeriod() {
            Bukkit.getScheduler().runTask(PlexDuels.getInstance(), () -> {
                if (player1 != null && player1.isOnline()) player1.playSound(player1.getLocation(), Sound.MUSIC_DISC_RELIC, 1f, 1f);
                if (player2 != null && player2.isOnline()) player2.playSound(player2.getLocation(), Sound.MUSIC_DISC_RELIC, 1f, 1f);
            });

            graceTask = new BukkitRunnable() {
                int time = 30;
                @Override
                public void run() {
                    boolean p1Online = player1 != null && player1.isOnline();
                    boolean p2Online = player2 != null && player2.isOnline();
                    if (time <= 0 || (!p1Online && !p2Online)) {
                        endGracePeriod();
                        return;
                    }
                    if (p1Online) player1.sendActionBar(ChatColor.RED + "Time left: " + time + "s");
                    if (p2Online) player2.sendActionBar(ChatColor.RED + "Time left: " + time + "s");
                    time--;
                }
            };
            graceTask.runTaskTimer(PlexDuels.getInstance(), 0L, 20L);
        }

        public void endGracePeriod() {
            if (graceTask != null) {
                graceTask.cancel();
                graceTask = null;
            }

            if (loser != null && loser.isOnline() && loser.getGameMode() == GameMode.SPECTATOR) loser.setGameMode(GameMode.SURVIVAL);
            if (player1 != null && player1.isOnline()) {
                player1.teleport(pos1);
                player1.stopSound(Sound.MUSIC_DISC_RELIC);
            }
            if (player2 != null && player2.isOnline()) {
                player2.teleport(pos2);
                player2.stopSound(Sound.MUSIC_DISC_RELIC);
            }

            if (player1 != null) DuelManager.getInstance().activeDuels.remove(player1.getUniqueId());
            if (player2 != null) DuelManager.getInstance().activeDuels.remove(player2.getUniqueId());
        }



        public boolean isInGracePeriod() { return !isActive && winner != null; }
    }
}