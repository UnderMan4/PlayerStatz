package pl.underman.playerstatz;

import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.underman.playerstatz.config.ApplicationConfig;
import pl.underman.playerstatz.hibernate.Database;
import pl.underman.playerstatz.pluginconfig.MainConfig;
import pl.underman.playerstatz.pluginconfig.TimelineModuleConfig;
import pl.underman.playerstatz.services.TimelineService;
import pl.underman.playerstatz.util.ApplicationContext;
import pl.underman.playerstatz.util.PluginConfigurationContext;

public final class PlayerStatz extends JavaPlugin {

    @Getter
    private static PlayerStatz instance;

    @Getter
    private static Database database;

    @Getter
    private static ApplicationContext applicationContext;

    private TimelineService timelineService;

    private PluginConfigurationContext pluginConfigurationContext;


    @Override
    public void onEnable() {
        instance                   = this;
        pluginConfigurationContext = new PluginConfigurationContext(ApplicationConfig.class);
        if (Boolean.FALSE.equals(getConfig(MainConfig.class).isDebugMode())) {
            Configurator.setLevel("org.hibernate", Level.OFF);
            Configurator.setLevel("org.reflections", Level.OFF);
        }
        applicationContext = new ApplicationContext(ApplicationConfig.class);
        timelineService    = applicationContext.getComponentInstance(TimelineService.class);
        database           = applicationContext.getComponentInstance(Database.class);
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
        if (pluginConfigurationContext.getConfig(TimelineModuleConfig.class)
                .isEnablePlayerActivity()) {
            timelineService.endAllPlayersSessions();
        }
    }

    public <T> T getConfig(Class<T> clazz) {
        return pluginConfigurationContext.getConfig(clazz);
    }
}
