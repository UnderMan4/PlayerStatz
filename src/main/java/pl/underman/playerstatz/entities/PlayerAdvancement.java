package pl.underman.playerstatz.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

@Entity(name = "player_advancement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlayerAdvancement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plugin_player_id")
    @NotNull
    @Cascade({org.hibernate.annotations.CascadeType.PERSIST, CascadeType.MERGE})
    PluginPlayer pluginPlayer;

    @Column(name = "advancement_namespace")
    String advancementNamespace;

    @Column(name = "advancement_key")
    String advancementKey;

    @Column(name = "advancement_date")
    LocalDateTime advancementDate;
}
