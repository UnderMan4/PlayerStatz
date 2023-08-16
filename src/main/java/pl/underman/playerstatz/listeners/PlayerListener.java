package pl.underman.playerstatz.listeners;

import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.entities.PluginPlayer;

import java.time.LocalDateTime;

@Log
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
//        PluginPlayer pluginPlayer = PlayerStatz.getDatabase().find(PluginPlayer.class, "uuid", player.getUniqueId());
//        log.log(java.util.logging.Level.INFO, pluginPlayer.toString());
//        if (pluginPlayer == null) {
//            pluginPlayer = PluginPlayer.builder().uuid(player.getUniqueId()).lastLogin(LocalDateTime.now()).build();
//            PlayerStatz.getDatabase().persist(pluginPlayer);
//        } else {
//            pluginPlayer.setLastLogin(LocalDateTime.now());
//            PlayerStatz.getDatabase().update(PluginPlayer.class, pluginPlayer.getId(), pluginPlayer);
//        }
        PluginPlayer pluginPlayer = PluginPlayer.builder().uuid(player.getUniqueId()).lastLogin(LocalDateTime.now()).build();
        PlayerStatz.getDatabase().persist(pluginPlayer);
    }
}
