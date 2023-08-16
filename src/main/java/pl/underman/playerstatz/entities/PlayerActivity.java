package pl.underman.playerstatz.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PlayerActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Boolean isAfk;
    LocalDateTime start;
    LocalDateTime end;

    @ManyToOne
    PluginPlayer pluginPlayer;
}
