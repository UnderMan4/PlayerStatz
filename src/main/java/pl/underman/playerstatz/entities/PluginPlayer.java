package pl.underman.playerstatz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "plugin_player")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PluginPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "uuid")
    UUID uuid;
    @Column(name = "username")
    String username;
    @Column(name = "last_login")
    LocalDateTime lastLogin;
    @Column(name = "last_logout")
    LocalDateTime lastLogout;
    @Column(name = "is_online")
    Boolean isOnline;

    public PluginPlayer(Player player) {
        this.uuid      = player.getUniqueId();
        this.username  = player.getName();
        this.lastLogin = LocalDateTime.now();
    }
}
