package pl.underman.playerstatz.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.util.Logger;
import pl.underman.playerstatz.util.annotations.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlayerSessionRepository extends BaseRepository<PlayerSession> {


    @Override
    public PlayerSession getById(Long id) {
        return database.get(PlayerSession.class, id);
    }

    public PlayerSession getCurrentPlayerSession(Session session, UUID uuid) {
        CriteriaBuilder              builder  = session.getCriteriaBuilder();
        CriteriaQuery<PlayerSession> criteria = builder.createQuery(PlayerSession.class);
        Root<PlayerSession>          root     = criteria.from(PlayerSession.class);
        PlayerSession                result   = null;

        try {
            criteria.select(root)
                    .where(
                            builder.equal(root.get("pluginPlayer").get("uuid"), uuid),
                            builder.isNull(root.get("sessionEnd"))
                    );
            result = session.createQuery(criteria).getSingleResult();
        } catch (Exception e) {
            Logger.debug("PlayerActivityRepository.getCurrentPlayerActivity: " + e.getMessage());
        }
        return result;
    }

    public List<PlayerSession> getCurrentPlayerSession(Session session) {
        CriteriaBuilder              builder  = session.getCriteriaBuilder();
        CriteriaQuery<PlayerSession> criteria = builder.createQuery(PlayerSession.class);
        Root<PlayerSession>          root     = criteria.from(PlayerSession.class);
        List<PlayerSession>          result   = null;

        try {
            criteria.select(root).where(builder.isNull(root.get("sessionEnd")));
            result = session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            Logger.debug("PlayerActivityRepository.getCurrentPlayerActivity: " + e.getMessage());
        }
        return result;
    }

}
