package pl.underman.playerstatz;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.underman.playerstatz.hibernate.Database;
import pl.underman.playerstatz.listeners.PlayerListener;
import pl.underman.playerstatz.services.PlayerSessionService;

public final class PlayerStatz extends JavaPlugin {

    @Getter
    private static       PlayerStatz instance;
    @Getter
    private static  Database    database;

    private  PlayerSessionService playerSessionService;


    @Override
    public void onEnable() {
        instance = this;
        database = new Database();
        playerSessionService = new PlayerSessionService();

        registerListeners(new PlayerListener());
    }

    @Override
    public void onDisable() {
        endPlayerSessions();
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void endPlayerSessions() {
        playerSessionService.endAllPlayersSessions();
    }
}
