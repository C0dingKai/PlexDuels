package dev.kai.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class LocationUtil {

    public static void saveLocation(FileConfiguration config, String path, Location loc) {
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }

    public static Location loadLocation(FileConfiguration config, String path) {
        if (!config.contains(path + ".world")) return null;

        String world = config.getString(path + ".world");
        double x = config.getDouble(path + ".x");
        double y = config.getDouble(path + ".y");
        double z = config.getDouble(path + ".z");
        float yaw = (float) config.getDouble(path + ".yaw");
        float pitch = (float) config.getDouble(path + ".pitch");

        if (Bukkit.getWorld(world) == null) return null;

        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
