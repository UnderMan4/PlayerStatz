package pl.underman.playerstatz;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.underman.playerstatz.hibernate.Database;
import pl.underman.playerstatz.listeners.PlayerListener;

import java.util.List;
import java.util.logging.Logger;

public final class PlayerStatz extends JavaPlugin {

    @Getter
    private static PlayerStatz instance;
    @Getter
    private static Database database;



    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        database = new Database();

        registerListeners(new PlayerListener());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
