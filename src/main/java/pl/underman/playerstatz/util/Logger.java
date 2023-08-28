package pl.underman.playerstatz.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.pluginconfig.MainConfig;

import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logger {


    public static void info(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.INFO, message);
    }

    public static void debug(String message) {
        if (Boolean.TRUE.equals(PlayerStatz.getConfig(MainConfig.class).isDebugMode())) {
            PlayerStatz.getInstance().getLogger().log(Level.INFO, "[DEBUG] {0}", message);
        }
    }

    public static void warning(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.WARNING, message);
    }

    public static void error(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.SEVERE, message);
    }

    public static void log(Level level, String message) {
        PlayerStatz.getInstance().getLogger().log(level, message);
    }
}
