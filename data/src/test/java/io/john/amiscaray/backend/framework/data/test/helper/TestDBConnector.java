package io.john.amiscaray.backend.framework.data.test.helper;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public interface TestDBConnector <T, TID> {

    Connection getDBConnection() throws SQLException;

    T parseFromResultSet(ResultSet rs) throws SQLException;

    Class<T> getEntityType();

    default String getTableName() {
        var entityType = getEntityType();
        return entityType.isAnnotationPresent(Table.class) ? entityType.getAnnotation(Table.class).name() : entityType.getSimpleName();
    }

    default T queryById(TID id) throws SQLException {
        try (Statement statement = getDBConnection().createStatement()) {
            Field idField = Arrays.stream(getEntityType().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Id.class))
                    .findFirst().orElseThrow();
            assert idField.getType().equals(id.getClass());
            var resultSet = statement.executeQuery("SELECT * FROM " + getTableName() +" WHERE " + idField.getName() + " = " + id);
            if (resultSet.next()) {
                return parseFromResultSet(resultSet);
            }
            throw new NoSuchElementException("No element of type " + getEntityType().getName() + " exists with id " + id);
        }
    }

    default void runQueryFromFile(String file) throws SQLException, FileNotFoundException {
        RunScript.execute(getDBConnection(), new FileReader(Objects.requireNonNull(TestDBConnector.class.getResource(file)).getFile()));
    }

    default List<T> queryEntries(String sql) throws SQLException {
        try (Statement statement = getDBConnection().createStatement()) {
            var resultSet = statement.executeQuery(sql);
            var result = new ArrayList<T>();
            while(resultSet.next()) {
                result.add(parseFromResultSet(resultSet));
            }
            return result;
        }
    }

    default void clearTable() {
        try {
            try (var deleteStatement = getDBConnection().prepareCall("DELETE FROM " + getTableName() + " WHERE true")) {
                deleteStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
