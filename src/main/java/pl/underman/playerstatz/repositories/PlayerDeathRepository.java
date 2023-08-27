package pl.underman.playerstatz.repositories;

import pl.underman.playerstatz.entities.PlayerDeath;
import pl.underman.playerstatz.util.annotations.Component;

@Component
public class PlayerDeathRepository extends BaseRepository<PlayerDeath> {
    @Override
    public PlayerDeath getById(Long id) {
        return database.get(PlayerDeath.class, id);
    }
}
