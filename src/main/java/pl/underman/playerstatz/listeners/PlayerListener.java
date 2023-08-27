package pl.underman.playerstatz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hibernate.Session;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.pluginconfig.TimelineModuleConfig;
import pl.underman.playerstatz.repositories.PlayerSessionRepository;
import pl.underman.playerstatz.repositories.PluginPlayerRepository;
import pl.underman.playerstatz.services.TimelineService;
import pl.underman.playerstatz.util.Logger;
import pl.underman.playerstatz.util.annotations.Autowired;
import pl.underman.playerstatz.util.annotations.EventListener;

import java.time.LocalDateTime;

@EventListener
public class PlayerListener implements Listener {

    @Autowired
    private PluginPlayerRepository pluginPlayerRepository;

    @Autowired
    private PlayerSessionRepository playerSessionRepository;

    @Autowired
    private TimelineService timelineService;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (PlayerStatz.getInstance()
                .getConfig(TimelineModuleConfig.class)
                .isEnablePlayerActivity()) {
            Logger.debug("PlayerListener.onPlayerJoin");
            Player  player  = e.getPlayer();
            Session session = pluginPlayerRepository.startSession();
            session.beginTransaction();
            PluginPlayer pluginPlayer = pluginPlayerRepository.getPlayerByUuid(
                    session,
                    player.getUniqueId()
            );

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
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (PlayerStatz.getInstance()
                .getConfig(TimelineModuleConfig.class)
                .isEnablePlayerActivity()) {
            Logger.debug("PlayerListener.onPlayerQuit");
            timelineService.endPlayerSession(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (PlayerStatz.getInstance()
                .getConfig(TimelineModuleConfig.class)
                .isEnableDeathTracking()) {
            Logger.debug("PlayerListener.onPlayerDeath");
            
            Player            player            = e.getPlayer();
            EntityDamageEvent entityDamageEvent = player.getLastDamageCause();
            
            
            if (entityDamageEvent != null){
                EntityDamageEvent.DamageCause cause = entityDamageEvent.getCause();
                Logger.debug("PlayerListener.onPlayerDeath: deathMessage = " + e.getDeathMessage());
                Logger.debug("PlayerListener.onPlayerDeath: cause = " + cause);
            }
            
            timelineService.savePlayerDeath(player, e.getDeathMessage());
        }
    }

}
