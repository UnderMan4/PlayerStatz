package pl.underman.playerstatz.services;

import org.bukkit.entity.Player;
import org.hibernate.Session;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.pluginconfig.PlayerSessionModuleConfig;
import pl.underman.playerstatz.repositories.PlayerSessionRepository;
import pl.underman.playerstatz.util.Logger;
import pl.underman.playerstatz.util.annotations.Autowired;
import pl.underman.playerstatz.util.annotations.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PlayerSessionService {

    @Autowired
    private PlayerSessionRepository playerSessionRepository;

    public void endPlayerSession(Player player) {
        Session session = playerSessionRepository.startSession();
        session.beginTransaction();

        PlayerSession playerSession = playerSessionRepository.getCurrentPlayerSession(session, player.getUniqueId());

        PluginPlayer  pluginPlayer    = playerSession.getPluginPlayer();
        LocalDateTime sessionStart    = playerSession.getSessionStart();
        LocalDateTime sessionEnd      = LocalDateTime.now();
        long          sessionDuration = Duration.between(sessionStart, sessionEnd).toMillis();

        int minSessionTime = PlayerStatz.getInstance()
                .getConfig(PlayerSessionModuleConfig.class)
                .getMinSessionTime();

        Logger.debug("PlayerSessionService.endPlayerSession: sessionDuration = " + sessionDuration / 1000 + "s");
        Logger.debug("PlayerSessionService.endPlayerSession: minSessionTime = " + minSessionTime);

        if (sessionDuration / 1000 >= minSessionTime) {
            playerSession.setSessionEnd(sessionEnd);
            playerSession.setSessionDuration(sessionDuration);
            pluginPlayer.setLastLogout(sessionEnd);
            pluginPlayer.setIsOnline(false);
            playerSessionRepository.update(playerSession);
        }

        session.getTransaction().commit();
        session.close();
    }

    public void endAllPlayersSessions() {
        Logger.info("Ending all players sessions");
        Session session = playerSessionRepository.startSession();
        session.beginTransaction();
        LocalDateTime sessionEnd = LocalDateTime.now();

        int minSessionTime = PlayerStatz.getInstance()
                .getConfig(PlayerSessionModuleConfig.class)
                .getMinSessionTime();

        playerSessionRepository.getCurrentPlayerSession(session).forEach(playerSession -> {
            PluginPlayer  pluginPlayer    = playerSession.getPluginPlayer();
            LocalDateTime sessionStart    = playerSession.getSessionStart();
            long          sessionDuration = Duration.between(sessionStart, sessionEnd).toMillis();

            if (sessionDuration / 1000 < minSessionTime) {
                return;
            }

            playerSession.setSessionEnd(sessionEnd);
            playerSession.setSessionDuration(sessionDuration);
            pluginPlayer.setLastLogout(sessionEnd);
            pluginPlayer.setIsOnline(false);

            playerSessionRepository.update(playerSession);
        });

        session.getTransaction().commit();
        session.close();
    }
}
