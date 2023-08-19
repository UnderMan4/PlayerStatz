package pl.underman.playerstatz.services;

import org.bukkit.entity.Player;
import org.hibernate.Session;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.entities.PluginPlayer;
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

        PlayerSession playerSession = playerSessionRepository.getCurrentPlayerSession(
                session,
                player.getUniqueId()
        );

        PluginPlayer  pluginPlayer    = playerSession.getPluginPlayer();
        LocalDateTime sessionStart    = playerSession.getSessionStart();
        LocalDateTime sessionEnd      = LocalDateTime.now();
        Long          sessionDuration = Duration.between(sessionStart, sessionEnd).toMillis();


        playerSession.setSessionEnd(sessionEnd);
        playerSession.setSessionDuration(sessionDuration);
        pluginPlayer.setLastLogout(sessionEnd);
        pluginPlayer.setIsOnline(false);

        //TODO: Add minimum session duration handling

        playerSessionRepository.update(playerSession);

        session.getTransaction().commit();
        session.close();
    }

    public void endAllPlayersSessions() {
        Logger.info("Ending all players sessions");
        Session session = playerSessionRepository.startSession();
        session.beginTransaction();
        LocalDateTime sessionEnd = LocalDateTime.now();

        playerSessionRepository.getCurrentPlayerSession(session)
                .forEach(playerSession -> {
                    PluginPlayer  pluginPlayer    = playerSession.getPluginPlayer();
                    LocalDateTime sessionStart    = playerSession.getSessionStart();
                    Long          sessionDuration = Duration.between(sessionStart, sessionEnd).toMillis();

                    playerSession.setSessionEnd(sessionEnd);
                    playerSession.setSessionDuration(sessionDuration);
                    pluginPlayer.setLastLogout(sessionEnd);
                    pluginPlayer.setIsOnline(false);

                    playerSessionRepository.update(playerSession);
                });
    }
}
