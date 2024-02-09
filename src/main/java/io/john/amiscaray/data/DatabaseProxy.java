package io.john.amiscaray.data;

import io.john.amiscaray.web.application.properties.ApplicationProperties;
import jakarta.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Map;

import static io.john.amiscaray.web.application.properties.ApplicationProperty.*;
import static io.john.amiscaray.web.application.properties.ApplicationProperty.HBM2DDL;

public class DatabaseProxy {

    private final SessionFactory dbSessionFactory;
    private Session currentSession;

    public DatabaseProxy(ApplicationProperties properties, String classScanPackage) {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(Map.of(
                        SQL_DIALECT.getName(), properties.sqlDialect(),
                        DB_DRIVER_CLASS.getName(), properties.dbConnectionDriver(),
                        DB_CONNECTION_URL.getName(), properties.dbConnectionURL(),
                        DB_CONNECTION_USERNAME.getName(), properties.dbUsername(),
                        DB_CONNECTION_PASSWORD.getName(), properties.dbPassword(),
                        HBM2DDL.getName(), properties.hbm2ddl()
                ))
                .build();
        Reflections reflections = new Reflections(classScanPackage, Scanners.TypesAnnotated);
        MetadataSources sources = new MetadataSources(serviceRegistry);

        Metadata metadata;
        reflections.getTypesAnnotatedWith(Entity.class)
                .forEach(sources::addAnnotatedClass);

        metadata = sources.buildMetadata();

        dbSessionFactory = metadata.buildSessionFactory();
    }

    public void beginSession() {
        currentSession = dbSessionFactory.openSession();
    }

    public void endSession() {
        currentSession.close();
        currentSession = null;
    }

    public void persist(Object entity) {
        var transaction = currentSession.beginTransaction();
        currentSession.persist(entity);
        transaction.commit();
    }

    public <T> T fetchById(Object entityId, Class<T> entityType) {
        var transaction = currentSession.beginTransaction();
        var entity = currentSession.byId(entityType).getReference(entityId);
        transaction.commit();
        return entity;
    }

    public void delete(Object entityId, Class<?> entityType) {
        var transaction = currentSession.beginTransaction();
        var entity = currentSession.byId(entityType).getReference(entityId);
        currentSession.remove(entity);
        transaction.commit();
    }
}
