package io.john.amiscaray.data;

import io.john.amiscaray.data.helper.EmployeeTestDBConnector;
import io.john.amiscaray.data.helper.TestDBConnector;
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
    private static final TestDBConnector<Employee> testDBConnector = new EmployeeTestDBConnector(testApplicationProperties);
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
        testDBConnector.runQueryFromFile("/sql/sample/data.sql");

        var employeeToDelete = dbProxy.fetchById(1L, Employee.class);
        dbProxy.delete(employeeToDelete.getId(), Employee.class);
        List<Employee> resultingTable = testDBConnector.queryEntries("SELECT * FROM Employee");

        assertTrue(resultingTable.isEmpty());
    }

}
