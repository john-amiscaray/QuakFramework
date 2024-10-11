package io.john.amiscaray.quak.data.test.helper;

import io.john.amiscaray.quak.core.properties.ApplicationProperty;
import io.john.amiscaray.quak.data.test.stub.Employee;
import io.john.amiscaray.quak.core.properties.ApplicationProperties;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class EmployeeTestDBConnector implements TestDBConnector<Employee, Long> {

    private ApplicationProperties properties;

    @Override
    public Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.get(ApplicationProperty.DB_CONNECTION_URL),
                properties.get(ApplicationProperty.DB_CONNECTION_USERNAME),
                properties.get(ApplicationProperty.DB_CONNECTION_PASSWORD));
    }

    @Override
    public Employee parseFromResultSet(ResultSet rs) throws SQLException {
        return new Employee(rs.getLong("id"), rs.getString("name"), rs.getString("department"), rs.getLong("salary"));
    }

    @Override
    public Class<Employee> getEntityType() {
        return Employee.class;
    }
}
