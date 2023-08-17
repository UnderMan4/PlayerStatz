package pl.underman.playerstatz.repositories;

import org.hibernate.Session;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.hibernate.Database;

public abstract class BaseRepository<T> {

    protected static final Database database = PlayerStatz.getDatabase();
    public abstract T getById(Long id);

    public Session startSession(){
        return database.getSession();
    }
    public void update(T object){
        database.update(object);
    }

    public void update(Session session, T object){
        session.merge(object);
    }

    public void persist(T object){
        database.persist(object);
    }

    public void persist(Session session, T object){
        session.persist(object);
    }
}
