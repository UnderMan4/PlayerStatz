package pl.underman.playerstatz.services;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.hibernate.Session;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.entities.PlayerDeath;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.pluginconfig.TimelineModuleConfig;
import pl.underman.playerstatz.repositories.PlayerDeathRepository;
import pl.underman.playerstatz.repositories.PluginPlayerRepository;
import pl.underman.playerstatz.repositories.PlayerSessionRepository;
import pl.underman.playerstatz.util.Logger;
import pl.underman.playerstatz.util.annotations.Autowired;
import pl.underman.playerstatz.util.annotations.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TimelineService {

    @Autowired
    private PlayerSessionRepository playerSessionRepository;

    @Autowired
    private PluginPlayerRepository pluginPlayerService;

    @Autowired
    PlayerDeathRepository playerDeathRepository;


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
        long          sessionDuration = Duration.between(sessionStart, sessionEnd).toMillis();

        checkSessionDuration(sessionEnd, playerSession, pluginPlayer, sessionDuration);

        session.getTransaction().commit();
        session.close();
    }

    public void endAllPlayersSessions() {
        Logger.info("Ending all players sessions");
        Session session = playerSessionRepository.startSession();
        session.beginTransaction();
        LocalDateTime sessionEnd = LocalDateTime.now();


        playerSessionRepository.getCurrentPlayerSession(session).forEach(playerSession -> {
            PluginPlayer  pluginPlayer    = playerSession.getPluginPlayer();
            LocalDateTime sessionStart    = playerSession.getSessionStart();
            long          sessionDuration = Duration.between(sessionStart, sessionEnd).toMillis();

            checkSessionDuration(sessionEnd, playerSession, pluginPlayer, sessionDuration);
        });

        session.getTransaction().commit();
        session.close();
    }

    private void checkSessionDuration(
            LocalDateTime sessionEnd,
            PlayerSession playerSession,
            PluginPlayer pluginPlayer,
            long sessionDuration
    ) {
        int minSessionTime = PlayerStatz.getInstance()
                .getConfig(TimelineModuleConfig.class)
                .getMinSessionTime();
        long sessionDurationInSeconds = sessionDuration / 1000;

        Logger.debug("PlayerSessionService.checkSessionDuration: " + pluginPlayer.getUsername() +
                " sessionDuration = " + sessionDurationInSeconds + "s");

        if (sessionDurationInSeconds >= minSessionTime) {
            playerSession.setSessionEnd(sessionEnd);
            playerSession.setSessionDuration(sessionDuration);
            pluginPlayer.setLastLogout(sessionEnd);
            pluginPlayer.setIsOnline(false);

            playerSessionRepository.update(playerSession);
        } else {
            playerSessionRepository.delete(playerSession);
        }
    }

    public void savePlayerDeath(Player player, String deathMessage) {

        LocalDateTime                 deathTime         = LocalDateTime.now();
        EntityDamageEvent             entityDamageEvent = player.getLastDamageCause();
        Location                      location          = player.getLocation();
        EntityDamageEvent.DamageCause deathCause        = null;
        if (entityDamageEvent != null) {
            deathCause = entityDamageEvent.getCause();
        }

        Session session = playerSessionRepository.startSession();
        session.beginTransaction();

        PluginPlayer pluginPlayer = pluginPlayerService.getPlayerByUuid(
                session,
                player.getUniqueId()
        );

        PlayerDeath.PlayerDeathBuilder playerDeathBuilder = PlayerDeath.builder();

        playerDeathBuilder.deathMessage(deathMessage);
        playerDeathBuilder.deathCause(deathCause);
        playerDeathBuilder.deathTime(deathTime);
        playerDeathBuilder.pluginPlayer(pluginPlayer);

        if (PlayerStatz.getInstance()
                .getConfig(TimelineModuleConfig.class)
                .isSaveDeathLocation()) {
            playerDeathBuilder.deathLocationX(location.getX());
            playerDeathBuilder.deathLocationY(location.getY());
            playerDeathBuilder.deathLocationZ(location.getZ());
            playerDeathBuilder.deathLocationWorld(location.getWorld().getName());
        }

        PlayerDeath playerDeath = playerDeathBuilder.build();

        playerDeathRepository.persist(session, playerDeath);


        session.getTransaction().commit();
        session.close();
    }


}
