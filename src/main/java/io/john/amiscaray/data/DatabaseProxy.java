package io.john.amiscaray.data;

import io.john.amiscaray.data.query.QueryCriteria;
import io.john.amiscaray.data.update.FieldUpdate;
import io.john.amiscaray.web.application.properties.ApplicationProperties;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static io.john.amiscaray.web.application.properties.ApplicationProperty.*;

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
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.persist(entity);
        transaction.commit();
    }

    public void update(Object entity) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.merge(entity);
        transaction.commit();
    }

    public <T> T fetchById(Object entityId, Class<T> entityType) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        var entity = currentSession.byId(entityType).getReference(entityId);
        transaction.commit();
        return entity;
    }

    public void delete(Object entityId, Class<?> entityType) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        var entity = currentSession.byId(entityType).getReference(entityId);
        currentSession.remove(entity);
        transaction.commit();
    }

    public static DatabaseQuery.DatabaseQueryBuilder queryBuilder() {
        return DatabaseQuery.builder();
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

        for (QueryCriteria criteria : databaseQuery.criteria) {
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

        delete.where(deletionCriteria.criteria.stream()
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

        update.set(fieldUpdate.fieldName(), fieldUpdate.updateExpression().createExpression(root, cb));

        for (QueryCriteria criteria : updateCriteria.criteria) {
            update.where(criteria.getTestPredicate(root, cb));
        }

        currentSession.createMutationQuery(update).executeUpdate();
        transaction.commit();
    }

    public final <T, F> void updateAll(Class<T> entityType, FieldUpdate<F> fieldUpdate) {
        updateAll(entityType, DatabaseQuery.builder().build(), fieldUpdate);
    }

    public <T, V> void updateAll(Class<T> entityType, String fieldToUpdate, Class<V> fieldType, DatabaseQuery updateCriteria, V newValue) {
        updateAll(entityType, updateCriteria, FieldUpdate.setFieldToValue(fieldToUpdate, newValue, fieldType));
    }

    public <T, V> void updateAll(Class<T> entityType, String fieldToUpdate, Class<V> fieldType, V newValue) {
        updateAll(entityType, fieldToUpdate, fieldType, DatabaseQuery.builder().build(), newValue);
    }

    private void checkSessionStarted() {
        if (currentSession == null) {
            throw new IllegalStateException("Attempted to access database without an active session");
        }
    }

    private record DatabaseQuery(List<QueryCriteria> criteria) {

        public static DatabaseQueryBuilder builder() {
            return new DatabaseQueryBuilder();
        }

        public static class DatabaseQueryBuilder {
            private ArrayList<QueryCriteria> criteria;

            DatabaseQueryBuilder() {
            }

            public DatabaseQueryBuilder withCriteria(QueryCriteria criteria) {
                if (this.criteria == null) this.criteria = new ArrayList<>();
                this.criteria.add(criteria);
                return this;
            }

            public DatabaseQueryBuilder withCriteria(Collection<? extends QueryCriteria> criteria) {
                if (criteria == null) {
                    throw new NullPointerException("criteria cannot be null");
                }
                if (this.criteria == null) this.criteria = new ArrayList<>();
                this.criteria.addAll(criteria);
                return this;
            }

            public DatabaseQueryBuilder clearCriteria() {
                if (this.criteria != null)
                    this.criteria.clear();
                return this;
            }

            public DatabaseQuery build() {
                List<QueryCriteria> criteria;
                switch (this.criteria == null ? 0 : this.criteria.size()) {
                    case 0:
                        criteria = java.util.Collections.emptyList();
                        break;
                    case 1:
                        criteria = java.util.Collections.singletonList(this.criteria.get(0));
                        break;
                    default:
                        criteria = java.util.Collections.unmodifiableList(new ArrayList<>(this.criteria));
                }

                return new DatabaseQuery(criteria);
            }

            public String toString() {
                return "DatabaseProxy.DatabaseQuery.DatabaseQueryBuilder(criteria=" + this.criteria + ")";
            }
        }
    }

}
