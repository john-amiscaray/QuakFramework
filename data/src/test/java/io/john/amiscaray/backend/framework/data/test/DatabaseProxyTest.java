package io.john.amiscaray.backend.framework.data.test;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.data.DatabaseProxy;
import io.john.amiscaray.backend.framework.data.query.numeric.*;
import io.john.amiscaray.backend.framework.data.query.string.ValueContaining;
import io.john.amiscaray.backend.framework.data.query.string.ValueEndsWith;
import io.john.amiscaray.backend.framework.data.query.string.ValueLike;
import io.john.amiscaray.backend.framework.data.query.string.ValueStartsWith;
import io.john.amiscaray.backend.framework.data.test.stub.Employee;
import io.john.amiscaray.backend.framework.data.test.helper.EmployeeTestDBConnector;
import io.john.amiscaray.backend.framework.data.query.ValueIs;
import io.john.amiscaray.backend.framework.data.query.ValueIsOneOf;
import io.john.amiscaray.backend.framework.data.update.UpdateExpression;
import io.john.amiscaray.backend.framework.data.update.numeric.CompoundNumericFieldUpdate;
import io.john.amiscaray.backend.framework.data.update.numeric.ProductFieldUpdate;
import io.john.amiscaray.backend.framework.data.update.numeric.QuotientFieldUpdate;
import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static io.john.amiscaray.backend.framework.data.update.numeric.CompoundNumericFieldUpdate.*;

public class DatabaseProxyTest {

    private static final String hibernatePackage = "io.john.amiscaray.backend.framework.data.test.stub";
    private static final EmployeeTestDBConnector testDBConnector;
    private static DatabaseProxy dbProxy;

    static {

        try {
            var application = new Application(DatabaseProxyTest.class, new String[] {}) {
                @Override
                public void finish() {

                }

                @Override
                protected void startUp() {

                }
            };
            application.start();
            testDBConnector = new EmployeeTestDBConnector(ApplicationProperties.getInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @BeforeEach
    void cleanDB() throws SQLException, FileNotFoundException {
        dbProxy = new DatabaseProxy(hibernatePackage);
        testDBConnector.clearTable();
        dbProxy.beginSession();
        testDBConnector.runQueryFromFile("/sql/sample/employee_sample_data.sql");
    }

    @AfterEach
    void endSession() {
        dbProxy.endSession();
    }

    @Test
    void testEmployeeCanBeSaved() throws SQLException {
        var employee = new Employee("Dale", "Tech");
        testDBConnector.clearTable();

        dbProxy.persist(employee);
        var result = testDBConnector.queryEntries("SELECT * FROM Employee");

        assertEquals(List.of(
                employee
        ), result);
    }

    @Test
    void testEmployeeCanBeDeleted() throws SQLException{
        var employeeToDelete = testDBConnector.queryById(1L);
        dbProxy.delete(employeeToDelete.getId(), Employee.class);
        List<Employee> resultingTable = testDBConnector.queryEntries("SELECT * FROM Employee");

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate"),
                new Employee(5L, "Jeff", "Corporate")
        ), resultingTable);
    }

    @Test
    void testEmployeeCanBeQueriedById() {
        var fetchedEmployee = dbProxy.fetchById(1L, Employee.class);

        assertEquals(new Employee(1L, "Billy", "Tech"), fetchedEmployee);
    }

    @Test
    void testQueryingByEmployeeWithNonExistentIDReturnsNull() {
        assertNull(dbProxy.fetchById(21L, Employee.class));
    }

    @Test
    void testEmployeeCanBeQueriedByIdsBetween2And4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueBetween("id", 2, 4))
                .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testEmployeeCanBeQueriedByIdsGreaterThanOrEqualTo2AndLessThanOrEqualTo4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueGreaterThanOrEqualTo("id", 2)
                        .and(new ValueLessThanOrEqualTo("id", 4)))
                .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeByIdLessThan4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueLessThan("id", 4))
                .build());

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeByIdGreaterThan4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseProxy
                .queryBuilder()
                .withCriteria(new ValueGreaterThan("id", 4))
                .build());

        assertEquals(List.of(
                new Employee(5L, "Jeff", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testEmployeeQueryNameIsJohn() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("name", "John"))
                        .build());

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeWhereNameIsOneOfJohnOrElli() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIsOneOf("name", "John", "Elli"))
                        .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameStartsWithJo() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueStartsWith("name", "Jo"))
                        .build());

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameEndsWithHN() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueEndsWith("name", "hn"))
                        .build());

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameContainsLL() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueContaining("name", "ll"))
                        .build());

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech")
        ), fetchedEmployees);
    }

    @Test
    public void testQueryEmployeeNameContainsLLAndStartsWithEAsTwoSeparateCriteria() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueContaining("name", "ll"))
                        .withCriteria(new ValueStartsWith("name", "E"))
                        .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameLike() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueLike("name", "J_ff"))
                        .build());

        assertEquals(List.of(
                new Employee(5L, "Jeff", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testDeleteByNonExistentIDThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> dbProxy.delete(21L, Employee.class));
    }

    @Test
    void testDeleteEmployeeWithDepartmentCorporate() throws SQLException {
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
    void testDeleteEmployeeWithIdsLessThan2orGreaterThan3() throws SQLException {
        dbProxy.deleteAll(DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueLessThan("id", 2)
                                .or(new ValueGreaterThan("id", 3)))
                .build(), Employee.class);

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeWithDepartmentTechToTechnology() throws SQLException {
        dbProxy.updateAll(Employee.class, "department", String.class, DatabaseProxy.queryBuilder()
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
    void testUpdateEmployeeNamedJohnToJohnnyUsingUpdateMethod() throws SQLException {
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
    void testUpdateEmployeesInCorporateDepartmentToDoubleSalary() throws SQLException {
        dbProxy.updateAll(Employee.class,
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("department","Corporate"))
                        .build(),
                new ProductFieldUpdate<>("salary", Long.class, UpdateExpression.literal(2)));

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Corporate", 80000L),
                new Employee(5L, "Jeff", "Corporate", 80000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeesInTechDepartmentToHalveSalary() throws SQLException {
        dbProxy.updateAll(Employee.class,
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueIs("department","Tech"))
                        .build(),
                new QuotientFieldUpdate<>("salary", Long.class, UpdateExpression.literal(2)));

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 20000L),
                new Employee(2L, "Elli", "Tech", 20000L),
                new Employee(3L, "John", "Tech", 20000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeesToIncreaseSalaryBy50PercentAndAdd2000AsCompoundUpdate() throws SQLException {
        dbProxy.updateAll(Employee.class,
                CompoundNumericFieldUpdate
                        .<Long>builder()
                        .fieldName("salary")
                        .fieldType(Long.class)
                        .apply(new SubOperation<>(SubOperationType.PROD, 1.5))
                        .apply(new SubOperation<>(SubOperationType.SUM, 2000))
                        .build()
                );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 62000L),
                new Employee(2L, "Elli", "Tech", 62000L),
                new Employee(3L, "John", "Tech", 62000L),
                new Employee(4L, "Annie", "Corporate", 62000L),
                new Employee(5L, "Jeff", "Corporate", 62000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeSalaryDividingItBy9KeepsValueAsLong() throws SQLException {
        dbProxy.updateAll(Employee.class,
                new QuotientFieldUpdate<>("salary", Long.class, UpdateExpression.literal(9))
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 4444L),
                new Employee(2L, "Elli", "Tech", 4444L),
                new Employee(3L, "John", "Tech", 4444L),
                new Employee(4L, "Annie", "Corporate", 4444L),
                new Employee(5L, "Jeff", "Corporate", 4444L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testQueryEmployeeByIDGreaterThan2AndDepartmentIsCorporateAsConjunction() {
        var resultSet = dbProxy.queryAll(Employee.class, DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueGreaterThan("id", 2).and(new ValueIs("department", "Corporate")))
                        .build());

        assertEquals(List.of(
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), resultSet);
    }

    @Test
    public void testQueryEmployeeByIDGreaterThan2AndDepartmentIsCorporateAsSeparateCriteria() {
        var resultSet = dbProxy.queryAll(Employee.class, DatabaseProxy.queryBuilder()
                .withCriteria(new ValueGreaterThan("id", 2))
                .withCriteria(new ValueIs("department", "Corporate"))
                .build());

        assertEquals(List.of(
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), resultSet);
    }

    @Test
    public void testUpdateEmployeeWithIdGreaterThan2AndDepartmentIsCorporateToHaveDepartmentAsExecutive() throws SQLException {
        dbProxy.updateAll(Employee.class,
                "department",
                String.class,
                DatabaseProxy.queryBuilder()
                        .withCriteria(new ValueGreaterThan("department", 2))
                        .withCriteria(new ValueIs("department", "Corporate"))
                        .build(),
                "Executive");

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Executive", 40000L),
                new Employee(5L, "Jeff", "Executive", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }
}
