package io.john.amiscaray.backend.framework.data.test;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.data.DatabaseProxy;
import io.john.amiscaray.backend.framework.data.query.DatabaseQuery;
import io.john.amiscaray.backend.framework.data.test.stub.Employee;
import io.john.amiscaray.backend.framework.data.test.helper.EmployeeTestDBConnector;
import io.john.amiscaray.backend.framework.data.update.FieldUpdate;
import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;
import org.junit.jupiter.api.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static io.john.amiscaray.backend.framework.data.query.QueryCriteria.*;
import static io.john.amiscaray.backend.framework.data.update.UpdateExpression.*;
import static java.lang.Math.pow;
import static org.junit.jupiter.api.Assertions.*;

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
    void testExistsEmployeeWithIDOf1() {
        assertTrue(dbProxy.existsById(1, Employee.class));
    }

    @Test
    void testDoesNotExistEmployeeWithIDOf82() {
        assertFalse(dbProxy.existsById(82, Employee.class));
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
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseQuery.builder()
                .withCriteria(valueOfField("id", isBetween(2, 4)))
                .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testEmployeeCanBeQueriedByIdsGreaterThanOrEqualTo2AndLessThanOrEqualTo4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseQuery
                .builder()
                .withCriteria(valueOfField("id", isGreaterThanOrEqualTo(2)).and(valueOfField("id", isLessThanOrEqualTo(4))))
                .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testEmployeeCanBeQueriedUsingAllOfIDGreaterThanOrEqualTo2AndLessThanOrEqualTo4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseQuery
                .builder()
                .withCriteria(valueOfField("id", matchesAllOf(
                        isGreaterThanOrEqualTo(2),
                        isLessThanOrEqualTo(4)
                )))
                .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech"),
                new Employee(4L, "Annie", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeByIdLessThan4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseQuery.builder()
                .withCriteria(valueOfField("id", isLessThan(4)))
                .build());

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeByIdGreaterThan4() {
        var fetchedEmployees = dbProxy.queryAll(Employee.class, DatabaseQuery.builder()
                .withCriteria(valueOfField("id", isGreaterThan(4)))
                .build());

        assertEquals(List.of(
                new Employee(5L, "Jeff", "Corporate")
        ), fetchedEmployees);
    }

    @Test
    void testEmployeeQueryNameIsJohn() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                        .withCriteria(valueOfField("name", is("John")))
                        .build());

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeWhereNameIsOneOfJohnOrElli() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                        .withCriteria(valueOfField("name", isOneOf("John", "Elli")))
                        .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameStartsWithJo() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                         .withCriteria(valueOfField("name", startsWith("Jo")))
                        .build());

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameEndsWithHN() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                         .withCriteria(valueOfField("name", endsWith("hn")))
                        .build());

        assertEquals(List.of(new Employee(3L, "John", "Tech")), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameContainsLL() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                        .withCriteria(valueOfField("name", contains("ll")))
                        .build());

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech")
        ), fetchedEmployees);
    }

    @Test
    public void testQueryEmployeeNameContainsLLAndStartsWithEAsTwoSeparateCriteria() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                        .withCriteria(valueOfField("name", contains("ll")))
                        .withCriteria(valueOfField("name", startsWith("E")))
                        .build());

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech")
        ), fetchedEmployees);
    }

    @Test
    void testQueryEmployeeNameLike() {
        var fetchedEmployees = dbProxy.queryAll(
                Employee.class, DatabaseQuery.builder()
                        .withCriteria(valueOfField("name", isLike("J_ff")))
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
        dbProxy.deleteAll(DatabaseQuery.builder()
                        .withCriteria(valueOfField("department", is("Corporate")))
                        .build(), Employee.class);

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testDeleteEmployeeWithIdsLessThan2orGreaterThan3() throws SQLException {
        dbProxy.deleteAll(DatabaseQuery.builder()
                        .withCriteria(valueOfField("id", isLessThan(2))
                                .or(valueOfField("id", isGreaterThan(3))))
                .build(), Employee.class);

        assertEquals(List.of(
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "John", "Tech")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeWithDepartmentTechToTechnology() throws SQLException {
        dbProxy.updateAll(Employee.class,
                "department",
                DatabaseQuery.builder()
                        .withCriteria(valueOfField("department", is("Tech")))
                        .build(),
                "Technology");

        assertEquals(List.of(
                new Employee(1L, "Billy", "Technology"),
                new Employee(2L, "Elli", "Technology"),
                new Employee(3L, "John", "Technology"),
                new Employee(4L, "Annie", "Corporate"),
                new Employee(5L, "Jeff", "Corporate")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeNamedJohnToJohnnyUsingPutMethod() throws SQLException {
        var isUpdate = dbProxy.put(new Employee(3L, "Johnny", "Tech"), 3L, Employee.class);

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech"),
                new Employee(2L, "Elli", "Tech"),
                new Employee(3L, "Johnny", "Tech"),
                new Employee(4L, "Annie", "Corporate"),
                new Employee(5L, "Jeff", "Corporate")
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
        assertTrue(isUpdate);
    }

    @Test
    void testUpdateEmployeesInCorporateDepartmentToDoubleSalary() throws SQLException {
        dbProxy.updateAll(Employee.class,
                DatabaseQuery.builder()
                        .withCriteria(valueOfField("department", is("Corporate")))
                        .build(),
                FieldUpdate.<Number>builder("salary")
                        .apply(multiply(2))
                        .build());

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
                DatabaseQuery.builder()
                        .withCriteria(valueOfField("department", is("Tech")))
                        .build(),
                FieldUpdate.<Number>builder("salary")
                        .apply(divide(2))
                        .build());

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 20000L),
                new Employee(2L, "Elli", "Tech", 20000L),
                new Employee(3L, "John", "Tech", 20000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeesToIncreaseSalaryBy50PercentAndAdd2000() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Number>builder("salary")
                        .apply(multiply(1.5))
                        .apply(add(2000))
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
    void testUpdateEmployeesToSubtract10000FromSalary() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Number>builder("salary")
                        .apply(subtract(10000))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 30000L),
                new Employee(2L, "Elli", "Tech", 30000L),
                new Employee(3L, "John", "Tech", 30000L),
                new Employee(4L, "Annie", "Corporate", 30000L),
                new Employee(5L, "Jeff", "Corporate", 30000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    void testUpdateEmployeeSalaryDividingItBy9KeepsValueAsLong() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Number>builder("salary")
                        .apply(divide(9))
                        .build()
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
        var resultSet = dbProxy.queryAll(Employee.class, DatabaseQuery.builder()
                        .withCriteria(valueOfField("id", isGreaterThan(2)).and(valueOfField("department", is("Corporate"))))
                        .build());

        assertEquals(List.of(
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), resultSet);
    }

    @Test
    public void testQueryEmployeeByIDGreaterThan2AndDepartmentIsCorporateAsSeparateCriteria() {
        var resultSet = dbProxy.queryAll(Employee.class, DatabaseQuery.builder()
                .withCriteria(valueOfField("id", isGreaterThan(2)))
                .withCriteria(valueOfField("department", is("Corporate")))
                .build());

        assertEquals(List.of(
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), resultSet);
    }

    @Test
    public void testQueryEmployeeByDepartmentIsTechUsingSelectionQuery() {
        dbProxy.createSelectionQueryThen("FROM Employee WHERE department = 'Tech'", Employee.class, query -> {
            assertEquals(
                    List.of(
                            new Employee(1L, "Billy", "Tech", 40000L),
                            new Employee(2L, "Elli", "Tech", 40000L),
                            new Employee(3L, "John", "Tech", 40000L)
                    ),
                    query.getResultList()
            );
        });
    }

    @Test
    public void testUpdateEmployeeWithIdGreaterThan2AndDepartmentIsCorporateToHaveDepartmentAsExecutive() throws SQLException {
        dbProxy.updateAll(Employee.class,
                "department",
                DatabaseQuery.builder()
                        .withCriteria(valueOfField("id", isGreaterThan(2)))
                        .withCriteria(valueOfField("department", is("Corporate")))
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

    @Test
    public void testUpdateAllEmployeesSettingSalaryToZero() throws SQLException {
        dbProxy.updateAll(Employee.class,
                "salary",
                0);

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 0L),
                new Employee(2L, "Elli", "Tech", 0L),
                new Employee(3L, "John", "Tech", 0L),
                new Employee(4L, "Annie", "Corporate", 0L),
                new Employee(5L, "Jeff", "Corporate", 0L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesRaisingSalaryToThePowerOf2() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Double>builder("salary")
                        .apply(raiseToThePowerOf(2))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 1600000000L),
                new Employee(2L, "Elli", "Tech", 1600000000L),
                new Employee(3L, "John", "Tech", 1600000000L),
                new Employee(4L, "Annie", "Corporate", 1600000000L),
                new Employee(5L, "Jeff", "Corporate", 1600000000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesRaisingSalaryToThePowerOf2AndAdd5000() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Double>builder("salary")
                        .apply(raiseToThePowerOf(2))
                        .apply(add(5000.0))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 1600005000L),
                new Employee(2L, "Elli", "Tech", 1600005000L),
                new Employee(3L, "John", "Tech", 1600005000L),
                new Employee(4L, "Annie", "Corporate", 1600005000L),
                new Employee(5L, "Jeff", "Corporate", 1600005000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSetSalaryTo2ToThePowerOf15AndThenLogBase2() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Double>builder("salary")
                        .apply(setTo(pow(2.0, 15.0)))
                        .apply(logBaseN(2.0))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 15L),
                new Employee(2L, "Elli", "Tech", 15L),
                new Employee(3L, "John", "Tech", 15L),
                new Employee(4L, "Annie", "Corporate", 15L),
                new Employee(5L, "Jeff", "Corporate", 15L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSetSalaryToLnOfCurrentSalaryTimes200() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Double>builder("salary")
                        .apply(multiply(200.0))
                        .apply(ln())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 16L),
                new Employee(2L, "Elli", "Tech", 16L),
                new Employee(3L, "John", "Tech", 16L),
                new Employee(4L, "Annie", "Corporate", 16L),
                new Employee(5L, "Jeff", "Corporate", 16L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSetSalaryToSqrtOfCurrentSalary() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Double>builder("salary")
                        .apply(sqrt())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 200L),
                new Employee(2L, "Elli", "Tech", 200L),
                new Employee(3L, "John", "Tech", 200L),
                new Employee(4L, "Annie", "Corporate", 200L),
                new Employee(5L, "Jeff", "Corporate", 200L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSetSalaryToMultiplyByNegative2AndAbsoluteValue() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<Number>builder("salary")
                        .apply(multiply(-2L))
                        .apply(abs())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 80000L),
                new Employee(2L, "Elli", "Tech", 80000L),
                new Employee(3L, "John", "Tech", 80000L),
                new Employee(4L, "Annie", "Corporate", 80000L),
                new Employee(5L, "Jeff", "Corporate", 80000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSetSalaryToLogWithInvalidBase()  {
        assertThrows(IllegalArgumentException.class, () -> {
            dbProxy.updateAll(Employee.class,
                    FieldUpdate.<Double>builder("salary")
                            .apply(logBaseN(-2.0))
                            .build()
            );
        });
    }

    @Test
    public void testUpdateAllEmployeesAppendOneToDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(append(" One"))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech One", 40000L),
                new Employee(2L, "Elli", "Tech One", 40000L),
                new Employee(3L, "John", "Tech One", 40000L),
                new Employee(4L, "Annie", "Corporate One", 40000L),
                new Employee(5L, "Jeff", "Corporate One", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesPrependDeptOfToDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(prepend("Dept of "))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Dept of Tech", 40000L),
                new Employee(2L, "Elli", "Dept of Tech", 40000L),
                new Employee(3L, "John", "Dept of Tech", 40000L),
                new Employee(4L, "Annie", "Dept of Corporate", 40000L),
                new Employee(5L, "Jeff", "Dept of Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesAddTrailingAndLeadingWhitespaceAndTrimDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(append("      "))
                        .apply(prepend("     "))
                        .apply(trim())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesAddTrailingWhitespaceAndTrimTrailingDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(append("      "))
                        .apply(trimTrailing())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesAddTrailingAndLeadingWhitespaceAndTrimTrailingDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(append(" "))
                        .apply(prepend(" "))
                        .apply(trimTrailing())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", " Tech", 40000L),
                new Employee(2L, "Elli", " Tech", 40000L),
                new Employee(3L, "John", " Tech", 40000L),
                new Employee(4L, "Annie", " Corporate", 40000L),
                new Employee(5L, "Jeff", " Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesAddTrailingAndLeadingWhitespaceAndTrimLeadingDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(append(" "))
                        .apply(prepend(" "))
                        .apply(trimLeading())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech ", 40000L),
                new Employee(2L, "Elli", "Tech ", 40000L),
                new Employee(3L, "John", "Tech ", 40000L),
                new Employee(4L, "Annie", "Corporate ", 40000L),
                new Employee(5L, "Jeff", "Corporate ", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesAddLeadingWhitespaceAndTrimLeadingDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(prepend("      "))
                        .apply(trimLeading())
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSubstringFirstLetterOfDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(subString(0, 1))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "T", 40000L),
                new Employee(2L, "Elli", "T", 40000L),
                new Employee(3L, "John", "T", 40000L),
                new Employee(4L, "Annie", "C", 40000L),
                new Employee(5L, "Jeff", "C", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSubstringFrom3rdPosWithLen3OfDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(subString(1, 3))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "ech", 40000L),
                new Employee(2L, "Elli", "ech", 40000L),
                new Employee(3L, "John", "ech", 40000L),
                new Employee(4L, "Annie", "orp", 40000L),
                new Employee(5L, "Jeff", "orp", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSubstringFrom1ToEndOfDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(subString(1))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "ech", 40000L),
                new Employee(2L, "Elli", "ech", 40000L),
                new Employee(3L, "John", "ech", 40000L),
                new Employee(4L, "Annie", "orporate", 40000L),
                new Employee(5L, "Jeff", "orporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSubstringFrom1To100OfDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(subString(0, 100))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Corporate", 40000L),
                new Employee(5L, "Jeff", "Corporate", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeesSubstringFrom100To100OfDepartment() throws SQLException {
        dbProxy.updateAll(Employee.class,
                FieldUpdate.<String>builder("department")
                        .apply(subString(100, 100))
                        .build()
        );

        assertEquals(List.of(
                new Employee(1L, "Billy", "", 40000L),
                new Employee(2L, "Elli", "", 40000L),
                new Employee(3L, "John", "", 40000L),
                new Employee(4L, "Annie", "", 40000L),
                new Employee(5L, "Jeff", "", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

    @Test
    public void testUpdateAllEmployeeSetDepartmentToTechUsingMutationQuery() throws SQLException {
        dbProxy.createMutationQueryThen("UPDATE Employee e SET e.department = :newDepartment",
                query -> query.setParameter("newDepartment", "Tech")
                        .executeUpdate());

        assertEquals(List.of(
                new Employee(1L, "Billy", "Tech", 40000L),
                new Employee(2L, "Elli", "Tech", 40000L),
                new Employee(3L, "John", "Tech", 40000L),
                new Employee(4L, "Annie", "Tech", 40000L),
                new Employee(5L, "Jeff", "Tech", 40000L)
        ), testDBConnector.queryEntries("SELECT * FROM employee"));
    }

}
