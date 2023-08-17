package pl.underman.playerstatz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Entity(name = "player_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlayerSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "is_afk")
    @Builder.Default
    Boolean isAfk = false;

    @Column(name = "session_start")
    LocalDateTime sessionStart;

    @Column(name = "session_end")
    LocalDateTime sessionEnd;

    @Column(name = "session_duration")
    Long sessionDuration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plugin_player_id")
    @NotNull
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    PluginPlayer pluginPlayer;
}
