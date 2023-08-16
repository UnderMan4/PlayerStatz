package pl.underman.playerstatz.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PluginPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    UUID uuid;

    LocalDateTime lastLogin;
}
