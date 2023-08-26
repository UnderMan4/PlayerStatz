package pl.underman.playerstatz.hibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.underman.playerstatz.PlayerStatz;
import pl.underman.playerstatz.entities.PlayerSession;
import pl.underman.playerstatz.entities.PluginPlayer;
import pl.underman.playerstatz.pluginconfig.MainConfig;
import pl.underman.playerstatz.util.Logger;
import pl.underman.playerstatz.util.annotations.Component;

import java.util.List;

@Component
public class Database {
    SessionFactory sessionFactory;

    public Database() {
        sessionFactory = getConfiguration().buildSessionFactory();
    }

    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty(
                "hibernate.connection.url",
                "jdbc:h2:" + PlayerStatz.getInstance().getDataFolder().getAbsolutePath() + "/data/playerstatz"
        );
        configuration.setProperty("hibernate.connection.username", "sa");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.connection.batch_size", "16");
        configuration.setProperty("hibernate.connection.pool_size", "8");

        if (Boolean.TRUE.equals(PlayerStatz.getInstance().getConfig(MainConfig.class).isDebugMode())) {
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            configuration.setProperty("hibernate.use_sql_comments", "true");
        }

        configuration.addAnnotatedClass(PluginPlayer.class);
        configuration.addAnnotatedClass(PlayerSession.class);
        return configuration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }

    public void close() {
        sessionFactory.close();
    }


    public void persist(Object object) {
        Session session = getSession();
        session.beginTransaction();
        session.persist(object);
        session.getTransaction().commit();
        session.close();
    }

    public <T> T get(Class<T> clazz, Long id) {
        Session session = getSession();
        session.beginTransaction();
        T object = session.get(clazz, id);
        session.getTransaction().commit();
        session.close();
        return object;
    }

    public <T> void delete(Class<T> clazz, Long id) {
        Session session = getSession();
        session.beginTransaction();
        T object = session.get(clazz, id);
        session.remove(object);
        session.getTransaction().commit();
        session.close();
    }

    public <T> void update(T object) {
        Session session = getSession();
        session.beginTransaction();
        session.merge(object);
        session.getTransaction().commit();
        session.close();
    }

    public <T> T findOne(Session session, Class<T> clazz, String fieldName, Object value) {
        CriteriaBuilder  builder  = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T>          root     = criteria.from(clazz);
        T                result   = null;

        try {
            criteria.select(root).where(builder.equal(root.get(fieldName), value));
            result = session.createQuery(criteria).getSingleResult();
        } catch (Exception e) {
            Logger.debug("Database.findOne: " + e.getMessage());
        }
        return result;
    }

    public <T> T findOne(Session session, Class<T> clazz, Criteria... criteriaList) {
        CriteriaBuilder  builder  = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T>          root     = criteria.from(clazz);
        T                result   = null;

        try {
            criteria.select(root);
            for (Criteria criteria1 : criteriaList) {
                criteria.where(builder.equal(root.get(criteria1.fieldName()), criteria1.value()));
            }
            result = session.createQuery(criteria).getSingleResult();
        } catch (Exception e) {
            Logger.debug("Database.findOne: " + e.getMessage());
        }
        return result;
    }


    public <T> List<T> find(Session session, Class<T> clazz, String fieldName, Object value) {
        CriteriaBuilder  builder  = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T>          root     = criteria.from(clazz);
        List<T>          result   = null;

        try {
            criteria.select(root).where(builder.equal(root.get(fieldName), value));
            result = session.createQuery(criteria).getResultList();
        } catch (Exception e) {
            Logger.debug("Database.find: " + e.getMessage());
        }
        return result;
    }
}
