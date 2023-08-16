package pl.underman.playerstatz.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.entities.PlayerActivity;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.util.Logger;

import java.time.LocalDateTime;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PluginPlayer pluginPlayer = PlayerStatz.getDatabase().find(PluginPlayer.class, "uuid", player.getUniqueId());
        if (pluginPlayer == null) {
            Logger.info( "PlayerListener.onPlayerJoin: pluginPlayer = null");
            pluginPlayer = PluginPlayer.builder().uuid(player.getUniqueId()).lastLogin(LocalDateTime.now()).build();
            PlayerStatz.getDatabase().persist(pluginPlayer);
        } else {
            Logger.info( "PlayerListener.onPlayerJoin: pluginPlayer = " + pluginPlayer);
            pluginPlayer.setLastLogin(LocalDateTime.now());
            PlayerStatz.getDatabase().update(PluginPlayer.class, pluginPlayer.getId(), pluginPlayer);
        }
        PlayerActivity playerActivity = PlayerActivity.builder().isAfk(false).start(LocalDateTime.now()).pluginPlayer(pluginPlayer).build();
        PlayerStatz.getDatabase().persist(playerActivity);
    }

//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent e) {
//        Player player = e.getPlayer();
//        PluginPlayer pluginPlayer = PlayerStatz.getDatabase().find(PluginPlayer.class, "uuid", player.getUniqueId());
//        PlayerActivity playerActivity = PlayerActivity.builder().isAfk(false).start(LocalDateTime.now()).pluginPlayer(pluginPlayer).build();
//        PlayerStatz.getDatabase().persist(playerActivity);
//    }
}
