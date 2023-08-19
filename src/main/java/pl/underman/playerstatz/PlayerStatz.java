package pl.underman.playerstatz;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.underman.playerstatz.config.ApplicationConfig;
import pl.underman.playerstatz.hibernate.Database;
import pl.underman.playerstatz.services.PlayerSessionService;
import pl.underman.playerstatz.util.ApplicationContext;

public final class PlayerStatz extends JavaPlugin {

    @Getter
    private static PlayerStatz instance;

    @Getter
    private static Database database;

    @Getter
    private static ApplicationContext applicationContext;

    private PlayerSessionService playerSessionService;


    @Override
    public void onEnable() {
        instance             = this;
        applicationContext   = new ApplicationContext(ApplicationConfig.class);
        playerSessionService = applicationContext.getComponentInstance(PlayerSessionService.class);
        database             = applicationContext.getComponentInstance(Database.class);

        registerListeners();
    }

    @Override
    public void onDisable() {
        endPlayersSessions();
    }

    private void registerListeners() {
        for (Listener listener : applicationContext.getListenerInstances()) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    private void endPlayersSessions() {
        playerSessionService.endAllPlayersSessions();
    }
}
