package io.john.amiscaray.backend.framework.data;

import io.john.amiscaray.backend.framework.data.query.DatabaseQuery;
import io.john.amiscaray.backend.framework.data.query.QueryCriteria;
import io.john.amiscaray.backend.framework.data.update.FieldUpdate;
import jakarta.persistence.Entity;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.Map;

import static io.john.amiscaray.backend.framework.core.properties.ApplicationProperty.*;
import static io.john.amiscaray.backend.framework.data.update.UpdateExpression.setTo;

public class DatabaseProxy {

    private final SessionFactory dbSessionFactory;
    private Session currentSession;

    public DatabaseProxy(String classScanPackage) {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(Map.of(
                        SQL_DIALECT.getName(), SQL_DIALECT.getValue(),
                        DB_DRIVER_CLASS.getName(), DB_DRIVER_CLASS.getValue(),
                        DB_CONNECTION_URL.getName(), DB_CONNECTION_URL.getValue(),
                        DB_CONNECTION_USERNAME.getName(), DB_CONNECTION_USERNAME.getValue(),
                        DB_CONNECTION_PASSWORD.getName(), DB_CONNECTION_PASSWORD.getValue(),
                        HBM2DDL.getName(), HBM2DDL.getValue())
                )
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

    public <T> boolean existsById(Object entityID, Class<T> entityType) {
        checkSessionStarted();
        var optionalEntity = currentSession.byId(entityType).loadOptional(entityID);
        return optionalEntity.isPresent();
    }

    public void persist(Object entity) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.persist(entity);
        transaction.commit();
    }

    public <T> boolean put(Object entity, Object entityID, Class<T> entityType) {
        var isUpdate = existsById(entityID, entityType);

        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.merge(entity);
        transaction.commit();

        return isUpdate;
    }

    public <T> boolean patch(Object entity, Object entityID, Class<T> entityType) {
        var exists = existsById(entityID, entityType);
        if (!exists) {
            return false;
        }

        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.merge(entity);
        transaction.commit();

        return true;
    }

    public <T> T fetchById(Object entityId, Class<T> entityType) {
        checkSessionStarted();
        return currentSession.get(entityType, entityId);
    }

    public void delete(Object entityId, Class<?> entityType) {
        checkSessionStarted();
        var entity = fetchById(entityId, entityType);
        if (entity == null) {
            throw new IllegalArgumentException("Could not find entity of type " + entityType.getSimpleName() + " with ID: " + entityId);
        }
        var transaction = currentSession.beginTransaction();
        currentSession.remove(entity);
        transaction.commit();
    }

    public <T> List<T> queryAll(Class<T> entityType) {
        return queryAll(entityType, DatabaseQuery.builder().build());
    }

    public <T> List<T> queryAll(Class<T> entityType, DatabaseQuery databaseQuery) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<T> cr = cb.createQuery(entityType);
        Root<T> root = cr.from(entityType);
        var selection = cr.select(root);

        for (QueryCriteria criteria : databaseQuery.criteria()) {
            selection.where(criteria.getTestPredicate(root, cb));
        }

        Query<T> query = currentSession.createQuery(cr);
        var result = query.getResultList();
        transaction.commit();
        return result;
    }

    public <T> void deleteAll(DatabaseQuery deletionCriteria, Class<T> entityType) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaDelete<T> delete = cb.createCriteriaDelete(entityType);
        Root<T> root = delete.from(entityType);

        delete.where(deletionCriteria.criteria().stream()
                .map(criteria -> criteria.getTestPredicate(root, cb))
                .toArray(Predicate[]::new));

        currentSession.createMutationQuery(delete).executeUpdate();
        transaction.commit();
    }

    public final <T, F> void updateAll(Class<T> entityType, DatabaseQuery updateCriteria, FieldUpdate<F> fieldUpdate) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(entityType);
        Root<T> root = update.from(entityType);

        applyFieldUpdate(fieldUpdate, cb, update, root);

        for (QueryCriteria criteria : updateCriteria.criteria()) {
            update.where(criteria.getTestPredicate(root, cb));
        }

        currentSession.createMutationQuery(update).executeUpdate();
        transaction.commit();
    }

    public final <T, F> void updateAll(Class<T> entityType, FieldUpdate<F> fieldUpdate) {
        updateAll(entityType, DatabaseQuery.builder().build(), fieldUpdate);
    }

    public <T, V> void updateAll(Class<T> entityType, String fieldToUpdate, DatabaseQuery updateCriteria, V newValue) {
        updateAll(entityType, updateCriteria, FieldUpdate.builder().fieldName(fieldToUpdate).apply(setTo(newValue)).build());
    }

    public <T, V> void updateAll(Class<T> entityType, String fieldToUpdate, V newValue) {
        updateAll(entityType, fieldToUpdate, DatabaseQuery.builder().build(), newValue);
    }

    private <T, F> void applyFieldUpdate(
            FieldUpdate<F> fieldUpdate,
            CriteriaBuilder cb,
            CriteriaUpdate<T> update,
            Root<T> queryRoot) {
        Expression<F> currentExpression = queryRoot.get(fieldUpdate.fieldName());
        for (var updateExpression : fieldUpdate.updates()) {
            currentExpression = updateExpression.apply(currentExpression, queryRoot, cb);
        }
        update.set(fieldUpdate.fieldName(), currentExpression);
    }

    private void checkSessionStarted() {
        if (currentSession == null) {
            throw new IllegalStateException("Attempted to access database without an active session");
        }
    }

}
