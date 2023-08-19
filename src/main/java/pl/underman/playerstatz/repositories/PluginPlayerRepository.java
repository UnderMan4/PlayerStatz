package pl.underman.playerstatz.repositories;

import org.hibernate.Session;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.util.annotations.Component;

import java.util.UUID;

@Component
public class PluginPlayerRepository extends BaseRepository<PluginPlayer> {


    @Override
    public PluginPlayer getById(Long id) {
        return database.get(PluginPlayer.class, id);
    }

    public PluginPlayer getPlayerByUuid(UUID uuid) {
        Session      session = startSession();
        PluginPlayer result  = database.findOne(session, PluginPlayer.class, "uuid", uuid);
        session.close();
        return result;
    }

    public PluginPlayer getPlayerByUuid(Session session, UUID uuid) {
        return database.findOne(session, PluginPlayer.class, "uuid", uuid);
    }

    public PluginPlayer getPlayerByUsername(String username) {
        Session      session = startSession();
        PluginPlayer result  = database.findOne(session, PluginPlayer.class, "username", username);
        session.close();
        return result;
    }

    public PluginPlayer getPlayerByUsername(Session session, String username) {
        return database.findOne(session, PluginPlayer.class, "username", username);
    }
}
