package io.john.amiscaray.quak.data;

import io.john.amiscaray.quak.data.query.DatabaseQuery;
import io.john.amiscaray.quak.data.query.NativeQuery;
import io.john.amiscaray.quak.data.query.QueryCriteria;
import io.john.amiscaray.quak.data.update.FieldUpdate;
import io.john.amiscaray.quak.data.update.UpdateExpression;
import jakarta.persistence.Entity;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.hibernate.service.ServiceRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static io.john.amiscaray.quak.core.properties.ApplicationProperty.*;

/**
 * A class used to perform database operations. Used to create hibernate sessions and perform CRUD operations.
 */
public class DatabaseProxy {

    private final SessionFactory dbSessionFactory;
    private Session currentSession;

    /**
     * Initialize the DatabaseProxy. Looks in the project packages for hibernate entities.
     * @param classScanPackage The root package of the project to scan for entity classes from.
     */
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

    /**
     * Begins a hibernate session
     */
    public void beginSession() {
        currentSession = dbSessionFactory.openSession();
    }

    /**
     * Ends the hibernate session
     */
    public void endSession() {
        currentSession.close();
        currentSession = null;
    }

    /**
     * Checks if an entity exists based on its ID.
     * @param entityID The ID to test for.
     * @param entityType The class representing the database entity.
     * @return Whether there is an entity with the given ID.
     * @param <T> matches the type of the entity.
     */
    public <T> boolean existsById(Object entityID, Class<T> entityType) {
        checkSessionStarted();
        var optionalEntity = currentSession.byId(entityType).loadOptional(entityID);
        return optionalEntity.isPresent();
    }

    /**
     * Saves an entity to the database.
     * @param entity The entity.
     */
    public void persist(Object entity) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.persist(entity);
        transaction.commit();
    }

    /**
     * Performs a PUT operation on the database.
     * @param entity The entity to update or save to the database.
     * @param entityID The ID of the entity.
     * @param entityType The class of the entity.
     * @return Whether the operation was an update or a creation.
     * @param <T> The type of the entity.
     */
    public <T> boolean put(Object entity, Object entityID, Class<T> entityType) {
        var isUpdate = existsById(entityID, entityType);

        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        currentSession.merge(entity);
        transaction.commit();

        return isUpdate;
    }

    /**
     * Performs a PATCH operation on the database.
     * @param entity The updated entity.
     * @param entityID The ID of the entity to update.
     * @param entityType The type of the entity.
     * @return Whether the operation was successful.
     * @param <T> The type of the entity.
     */
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

    /**
     * Fetches an entity from the database by its ID.
     * @param entityId The ID of the entity.
     * @param entityType The type of the entity.
     * @return The entity.
     * @param <T> The type of the entity.
     */
    public <T> T fetchById(Object entityId, Class<T> entityType) {
        checkSessionStarted();
        return currentSession.get(entityType, entityId);
    }

    /**
     * Deletes an entity from the database by its ID.
     * @param entityId The ID of the entity.
     * @param entityType The type of the entity.
     */
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

    /**
     * Retrieves all entities of a given type from the database.
     * @param entityType The type of the entity.
     * @return A list of all the entities.
     * @param <T> The type of the entity.
     */
    public <T> List<T> queryAll(Class<T> entityType) {
        return queryAll(entityType, DatabaseQuery.builder().build());
    }

    /**
     * Retrieves all entities of a given type from the database which match a given query.
     * @param entityType The type of the entity.
     * @param databaseQuery The query to filter entities using.
     * @return A list of all the entities.
     * @param <T> The type of the entity.
     */
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

    /**
     * Retrieves all entities of a given type from the database which match a given query.
     * @param entityType The type of the entity.
     * @param criteria The query to filter entities using.
     * @return A list of all the entities.
     * @param <T> The type of the entity.
     */
    public <T> List<T> queryAllWhere(Class<T> entityType, QueryCriteria criteria) {
        return queryAll(entityType, new DatabaseQuery(List.of(criteria)));
    }

    /**
     * Deletes all entities from the database matching a given query.
     *
     * @param <T>              The type of the entity.
     * @param entityType       The type of the entity.
     * @param deletionCriteria A database query for the deletion criteria.
     */
    public <T> void deleteAll(Class<T> entityType, DatabaseQuery deletionCriteria) {
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

    /**
     * Deletes all entities from the database matching a given query.
     *
     * @param <T>              The type of the entity.
     * @param entityType       The type of the entity.
     * @param criteria         Query criteria for the deletion.
     */
    public <T> void deleteAllWhere(Class<T> entityType, QueryCriteria criteria) {
        deleteAll(entityType, new DatabaseQuery(List.of(criteria)));
    }

    /**
     * Updates all entities from the database matching some update criteria.
     * @param entityType The type of the entity.
     * @param updateCriteria A database query for the update criteria.
     * @param fieldUpdate The updates made to the field.
     * @param <T> The type of the entity.
     * @param <F> The type of the field being updated.
     */
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

    /**
     * Updates all entities from the database matching some update criteria.
     * @param entityType The type of the entity.
     * @param queryCriteria The query criteria to select rows to update.
     * @param fieldUpdate The updates made to the field.
     * @param <T> The type of the entity.
     * @param <F> The type of the field being updated.
     */
    public final <T, F> void updateAllWhereAndApply(Class<T> entityType, QueryCriteria queryCriteria, FieldUpdate<F> fieldUpdate) {
        updateAll(entityType, new DatabaseQuery(List.of(queryCriteria)), fieldUpdate);
    }

    /**
     * Updates all entities of the same type.
     * @param entityType The type of the entity.
     * @param fieldUpdate The updates made to the field.
     * @param <T> The type of the entity.
     * @param <F> The type of the field.
     */
    public final <T, F> void updateAll(Class<T> entityType, FieldUpdate<F> fieldUpdate) {
        updateAll(entityType, DatabaseQuery.builder().build(), fieldUpdate);
    }

    /**
     * Updates entities based on a given criteria to a new value.
     * @param entityType The type of the entity.
     * @param fieldToUpdate The name of the field to update.
     * @param updateCriteria A query for the update criteria.
     * @param newValue The new value to set to the field.
     * @param <T> The type of the entity.
     * @param <V> The type of the field.
     */
    public <T, V> void updateAll(Class<T> entityType, String fieldToUpdate, DatabaseQuery updateCriteria, V newValue) {
        updateAll(entityType, updateCriteria, FieldUpdate.builder(fieldToUpdate).apply(UpdateExpression.setTo(newValue)).build());
    }

    /**
     * Updates entities based on a given criteria to a new value.
     * @param entityType The type of the entity.
     * @param fieldToUpdate The name of the field to update.
     * @param updateCriteria The query criteria to select fields for the update.
     * @param newValue The new value to set to the field.
     * @param <T> The type of the entity.
     * @param <V> The type of the field.
     */
    public <T, V> void updateAllWhereAndSetTo(Class<T> entityType, String fieldToUpdate, QueryCriteria updateCriteria, V newValue) {
        updateAll(entityType, fieldToUpdate, new DatabaseQuery(List.of(updateCriteria)), newValue);
    }

    /**
     * Updates all entities of the same type with a new value.
     * @param entityType The type of the entity.
     * @param fieldToUpdate The name of the field to update.
     * @param newValue The new value of the field.
     * @param <T> The type of the entity.
     * @param <V> The type of the field.
     */
    public <T, V> void updateAll(Class<T> entityType, String fieldToUpdate, V newValue) {
        updateAll(entityType, fieldToUpdate, DatabaseQuery.builder().build(), newValue);
    }

    private <T, F> void applyFieldUpdate(
            FieldUpdate<F> fieldUpdate,
            CriteriaBuilder cb,
            CriteriaUpdate<T> update,
            Root<T> queryRoot) {
        Expression<F> currentExpression = queryRoot.get(fieldUpdate.fieldName());
        var fieldType = queryRoot.get(fieldUpdate.fieldName()).getJavaType();
        for (var updateExpression : fieldUpdate.updates()) {
            currentExpression = updateExpression.apply(currentExpression, queryRoot, cb);
        }
        update.set(fieldUpdate.fieldName(), currentExpression.as(fieldType));
    }

    /**
     * Creates a mutation query using an HQL string and runs it in a transaction. Example:<br>
     * <pre>
     * {@code dbProxy.createMutationQueryThen(
     *            "UPDATE Employee e SET e.department = :newDepartment",
     *            query -> query.setParameter("newDepartment", "Tech").executeUpdate()
     * );}
     * </pre>
     * @param hql The HQL query.
     * @param action A consumer of the created mutation query. Runs within the transaction.
     */
    public void createMutationQueryThen(String hql, Consumer<MutationQuery> action) {
        checkSessionStarted();
        var transaction = currentSession.beginTransaction();
        action.accept(currentSession.createMutationQuery(hql));
        transaction.commit();
    }

    /**
     * Creates a selection query from an HQL string. Example:<br>
     * <pre>
     * {@code dbProxy.createSelectionQueryThen("FROM Employee WHERE department = 'Tech'", Employee.class, query -> {
     *      assertEquals(
     *          List.of(
     *              new Employee(1L, "Billy", "Tech", 40000L),
     *              new Employee(2L, "Elli", "Tech", 40000L),
     *              new Employee(3L, "John", "Tech", 40000L)
     *          ),
     *          query.getResultList()
     *      );
     * });}
     * </pre>
     * @param hql The HQL string.
     * @param entityType The type of the entity.
     * @param action A consumer of the selection criteria.
     * @param <R> The type of the result.
     */
    public <R> void createSelectionQueryThen(String hql, Class<R> entityType, Consumer<SelectionQuery<R>> action) {
        checkSessionStarted();
        action.accept(currentSession.createSelectionQuery(hql, entityType));
    }

    /**
     * Creates a selection query from an HQL string and its parameters represented as an {@link io.john.amiscaray.quak.data.query.NativeQuery}.
     * @param query The query.
     * @param entityType The type of the entity.
     * @return The selection query.
     * @param <R> The type of the result.
     */
    public <R> SelectionQuery<R> createSelectionQuery(NativeQuery query, Class<R> entityType) {
        checkSessionStarted();
        var selectionQuery = currentSession.createSelectionQuery(query.hql(), entityType);

        for (var entry : query.params().entrySet()) {
            selectionQuery.setParameter(entry.getKey(), entry.getValue());
        }

        return selectionQuery;
    }

    private void checkSessionStarted() {
        if (currentSession == null) {
            throw new IllegalStateException("Attempted to access database without an active session");
        }
    }

}
