package pl.underman.playerstatz.util;

import pl.underman.playerstatz.PlayerStatz;

import java.util.logging.Level;

public class Logger {

    private Logger() {
    }

    public static void info(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.INFO, message);
    }
    public static void debug(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.INFO, "[DEBUG] {0}", message);
    }

    public static void warning(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.WARNING, message);
    }

    public static void severe(String message) {
        PlayerStatz.getInstance().getLogger().log(Level.SEVERE, message);
    }

    public static void log(Level level, String message) {
        PlayerStatz.getInstance().getLogger().log(level, message);
    }
}
