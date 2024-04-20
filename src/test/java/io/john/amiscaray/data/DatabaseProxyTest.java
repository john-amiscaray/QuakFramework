package io.john.amiscaray.data;

import io.john.amiscaray.data.helper.EmployeeTestDBConnector;
import io.john.amiscaray.data.query.ValueIs;
import io.john.amiscaray.data.query.ValueIsOneOf;
import io.john.amiscaray.data.query.numeric.ValueBetween;
import io.john.amiscaray.data.query.numeric.ValueGreaterThan;
import io.john.amiscaray.data.query.numeric.ValueLessThan;
import io.john.amiscaray.data.query.string.ValueContaining;
import io.john.amiscaray.data.query.string.ValueEndsWith;
import io.john.amiscaray.data.query.string.ValueLike;
import io.john.amiscaray.data.query.string.ValueStartsWith;
import io.john.amiscaray.data.stub.Employee;
import io.john.amiscaray.data.update.CompoundNumericFieldUpdate;
import io.john.amiscaray.data.update.UpdateExpression;
import io.john.amiscaray.data.update.numeric.ProductFieldUpdate;
import io.john.amiscaray.data.update.numeric.QuotientFieldUpdate;
import io.john.amiscaray.web.application.properties.ApplicationProperties;
import org.junit.jupiter.api.*;

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
    private static DatabaseProxy dbProxy;

    @BeforeEach
    void cleanDB() {
        dbProxy = new DatabaseProxy(testApplicationProperties, hibernatePackage);
        testDBConnector.clearTable();
        dbProxy.beginSession();
    }

    @AfterEach
    void endSession() {
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

        var fetchedEmployees = dbProxy.queryAll(DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueBetween("id", 2, 4))
                .build(), Employee.class);

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeByIdLessThan4() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueLessThan("id", 4))
                .build(), Employee.class);

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeByIdGreaterThan4() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueGreaterThan("id", 4))
                .build(), Employee.class);

        assertEquals(List.of(
                new Employee(5L, "Jeff", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testEmployeeQueryNameIsJohn() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("name", "John"))
                        .build(), Employee.class);

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeWhereNameIsOneOfJohnOrElli() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIsOneOf("name", "John", "Elli"))
                        .build(), Employee.class);

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameStartsWithJo() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueStartsWith("name", "Jo"))
                        .build(), Employee.class);

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameEndsWithHN() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueEndsWith("name", "hn"))
                        .build(), Employee.class);

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameContainsLL() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueContaining("name", "ll"))
                        .build(), Employee.class);

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameLike() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        var fetchedEmployees = dbProxy.queryAll(
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueLike("name", "J_ff"))
                        .build(), Employee.class);

        assertEquals(List.of(
                new Employee(5L, "Jeff", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testDeleteEmployeeWithDepartmentCorporate() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        dbProxy.deleteAll(DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("department", "Corporate"))
                        .build(), Employee.class);

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeWithDepartmentTechToTechnology() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        dbProxy.updateAll(Employee.class, "department", DatabaseProxy.queryBuilder()
                .withCriteria(new ValueIs("department", "Tech"))
                .build(), "Technology");

        assertEquals(List.of(
                new Employee(1L, "Billy", "Technology"),
                new Employee(2L, "Elli", "Technology"),
                new Employee(3L, "John", "Technology"),
                new Employee(4L, "Annie", "Corporate"),
                new Employee(5L, "Jeff", "Corporate")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeNamedJohnToJohnnyUsingUpdateMethod() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        dbProxy.update(new Employee(3L, "Johnny", "Tech"));

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "Johnny", "Tech"),
                new Employee(4L, "Annie", "Corporate"),
                new Employee(5L, "Jeff", "Corporate")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeesInCorporateDepartmentToDoubleSalary() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        dbProxy.updateAll(Employee.class,
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("department","Corporate"))
                        .build(),
                new ProductFieldUpdate<>("salary", UpdateExpression.literal(2L)));

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Corporate", 80000L),
                new Employee(5L, "Jeff", "Corporate", 80000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeesInTechDepartmentToHalveSalary() throws SQLException, FileNotFoundException {
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");

        dbProxy.updateAll(Employee.class,
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("department","Tech"))
                        .build(),
                new QuotientFieldUpdate("salary", UpdateExpression.literal(2L)));

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 20000L),
                new Employee(2L, "Elli", "Tech", 20000L),
                new Employee(3L, "John", "Tech", 20000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    @Disabled
    void testUpdateEmployeesToIncreaseSalaryBy50PercentAndAdd2000() throws SQLException, FileNotFoundException {
//        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");
//
//        dbProxy.updateAll(Employee.class,
//                DatabaseProxy.queryBuilder()
//                        .build(),
//                new CompoundNumericFieldUpdate<>("salary", List.of(
//                        new SimpleNumericFieldOperation<Number>()
//                )));
//
//        assertEquals(List.of(
//                new Employee(1L, "Billy", "Tech", 20000L),
//                new Employee(2L, "Elli", "Tech", 20000L),
//                new Employee(3L, "John", "Tech", 20000L),
//                new Employee(4L, "Annie", "Corporate", 40000L),
//                new Employee(5L, "Jeff", "Corporate", 40000L)
//        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }
}
