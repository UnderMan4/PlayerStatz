package pl.underman.playerstatz.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import pl.underman.playerstatz.PlayerStatz;

public class Database {
    SessionFactory sessionFactory;

    public Database() {
        sessionFactory = getConfiguration().buildSessionFactory();
    }

    public Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:" + PlayerStatz.getInstance().getDataFolder().getAbsolutePath() + "/data/playerstatz");
        configuration.setProperty("hibernate.connection.username", "sa");
        configuration.setProperty("hibernate.connection.password", "");
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.use_sql_comments", "true");
        configuration.addPackage("pl.underman.playerstatz.entities");
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

    public <T> void update(Class<T> clazz, Long id, T object) {
        Session session = getSession();
        session.beginTransaction();
        session.merge(object);
        session.getTransaction().commit();
        session.close();
    }

    public <T> T find(Class<T> clazz, String fieldName, Object value) {
        Session session = getSession();
        session.beginTransaction();
        T object = (T) session.createQuery("from " + clazz.getSimpleName() + " where " + fieldName + " = :value")
                .setParameter("value", value)
                .uniqueResult();
        session.getTransaction().commit();
        session.close();
        return object;
    }
}
