package io.john.amiscaray.data.helper;

import jakarta.persistence.Table;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface TestDBConnector <T> {

    Connection getDBConnection() throws SQLException;

    T parseFromResultSet(ResultSet rs) throws SQLException;

    Class<T> getEntityType();

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
            var entityType = getEntityType();
            String tableName = entityType.isAnnotationPresent(Table.class) ? entityType.getAnnotation(Table.class).name() : entityType.getSimpleName();
            try (var deleteStatement = getDBConnection().prepareCall("DELETE FROM " + tableName + " WHERE true")) {
                deleteStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
