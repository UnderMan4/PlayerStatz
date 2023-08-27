package pl.underman.playerstatz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Entity(name = "player_death")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlayerDeath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    
    @Column(name = "death_message")
    String deathMessage;
    
    @Column(name="death_cause")
    @Enumerated(EnumType.STRING)
    EntityDamageEvent.DamageCause deathCause;
    
    @Column(name = "death_time")
    LocalDateTime deathTime;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plugin_player_id")
    @NotNull
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    PluginPlayer pluginPlayer;
    
    @Column(name = "death_location_x")
    Double deathLocationX;
    
    @Column(name = "death_location_y")
    Double deathLocationY;
    
    @Column(name = "death_location_z")
    Double deathLocationZ;
    
    @Column(name = "death_location_world")
    String deathLocationWorld;
}
