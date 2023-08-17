package pl.underman.playerstatz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hibernate.Session;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.repositories.PlayerSessionRepository;
import pl.underman.playerstatz.repositories.PluginPlayerRepository;
import pl.underman.playerstatz.services.PlayerSessionService;
import pl.underman.playerstatz.util.Logger;

import java.time.LocalDateTime;

public class PlayerListener implements Listener {

    private final PluginPlayerRepository  pluginPlayerRepository  = new PluginPlayerRepository();
    private final PlayerSessionRepository playerSessionRepository = new PlayerSessionRepository();
    private final PlayerSessionService    playerSessionService    = new PlayerSessionService();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Logger.debug("PlayerListener.onPlayerJoin");
        Player  player  = e.getPlayer();
        Session session = pluginPlayerRepository.startSession();
        session.beginTransaction();
        PluginPlayer pluginPlayer = pluginPlayerRepository.getPlayerByUuid(session, player.getUniqueId());

        if (pluginPlayer == null) {
            Logger.debug("PlayerListener.onPlayerJoin: pluginPlayer = null");
            pluginPlayer = new PluginPlayer(player);
            pluginPlayer.setIsOnline(true);
            pluginPlayerRepository.persist(session, pluginPlayer);
        } else {
            Logger.debug("PlayerListener.onPlayerJoin: pluginPlayer = " + pluginPlayer);
            pluginPlayer.setLastLogin(LocalDateTime.now());
            pluginPlayer.setIsOnline(true);
            pluginPlayerRepository.update(session, pluginPlayer);
        }

        PlayerSession playerSession = PlayerSession.builder()
                .isAfk(false)
                .sessionStart(LocalDateTime.now())
                .pluginPlayer(pluginPlayer)
                .build();
        playerSessionRepository.persist(session, playerSession);
        session.getTransaction().commit();
        session.close();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Logger.debug("PlayerListener.onPlayerQuit");
        playerSessionService.endPlayerSession(e.getPlayer());
    }

}
