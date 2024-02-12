package io.john.amiscaray.data;

import io.john.amiscaray.data.helper.EmployeeTestDBConnector;
import io.john.amiscaray.data.query.numeric.ValueBetween;
import io.john.amiscaray.data.stub.Employee;
import io.john.amiscaray.web.application.properties.ApplicationProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseProxyTest {

    private static final ApplicationProperties testApplicationProperties = ApplicationProperties.builder()
            .dbConnectionDriver("org.h2.Driver")
            .dbConnectionURL("jdbc:h2:mem:test")
            .hbm2ddl("update")
            .dbUsername("sa")
            .dbPassword("")
            .sqlDialect("org.hibernate.dialect.H2Dialect")
            .build();
    private static final String hibernatePackage = "io.john.amiscaray.data.stub";
    private static final EmployeeTestDBConnector testDBConnector = new EmployeeTestDBConnector(testApplicationProperties);
    private final static DatabaseProxy dbProxy = new DatabaseProxy(testApplicationProperties, hibernatePackage);

    @BeforeEach
    void cleanDB() {
        testDBConnector.clearTable();
    }

    @BeforeAll
    static void startUp() {
        dbProxy.beginSession();
    }

    @AfterAll
    static void cleanUp() {
        dbProxy.endSession();
    }

    @Test
    void testEmployeeCanBeSaved() throws SQLException {
        var employee = new Employee("Billy", "Tech");

        dbProxy.persist(employee);
        var result = testDBConnector.queryEntries("SELECT * FROM Employee");

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), employee);
    }

    @Test
    void testEmployeeCanBeDeleted() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/single_employee.sql");

        var employeeToDelete = testDBConnector.queryById(1L);
        dbProxy.delete(employeeToDelete.getId(), Employee.class);
        List<Employee> resultingTable = testDBConnector.queryEntries("SELECT * FROM Employee");

        assertTrue(resultingTable.isEmpty());
    }

    @Test
    void testEmployeeCanBeQueriedById() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/single_employee.sql");

        var fetchedEmployee = dbProxy.fetchById(1L, Employee.class);

        assertEquals(new Employee(1L, "Billy", "Tech"), fetchedEmployee);
    }

    @Test
    void testEmployeeCanBeQueriedByIdsBetween() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.runQuery(DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueBetween("id", 2, 4))
                .build(), Employee.class);

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }
}
