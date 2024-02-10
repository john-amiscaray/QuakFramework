package io.john.amiscaray.data.helper;

import io.john.amiscaray.data.stub.Employee;
import io.john.amiscaray.web.application.properties.ApplicationProperties;
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
        return DriverManager.getConnection(properties.dbConnectionURL(), properties.dbUsername(), properties.dbPassword());
    }

    @Override
    public Employee parseFromResultSet(ResultSet rs) throws SQLException {
        return new Employee(rs.getLong("id"), rs.getString("name"), rs.getString("department"));
    }

    @Override
    public Class<Employee> getEntityType() {
        return Employee.class;
    }
}
