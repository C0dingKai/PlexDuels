package dev.kai;

import dev.kai.admin.SetDuelCommand;
import dev.kai.commands.DrawCommand;
import dev.kai.commands.DuelCommand;
import dev.kai.commands.LeaveCommand;
import dev.kai.listener.DuelCommandListener;
import dev.kai.manager.DuelManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlexDuels extends JavaPlugin {

    @Getter
    private static PlexDuels instance;

    @Override
    public void onEnable() {
        instance = this;

        DuelManager.getInstance().load(getConfig());

        new SetDuelCommand(this);
        new DuelCommand(this);
        new LeaveCommand(this);
        new DrawCommand(this);
        new DuelCommandListener(this);
    }

    @Override
    public void onDisable() {
        DuelManager.getInstance().save(getConfig());
        saveConfig();
    }
}
