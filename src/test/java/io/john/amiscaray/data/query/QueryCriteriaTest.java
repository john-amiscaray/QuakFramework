package io.john.amiscaray.data.query;

import io.john.amiscaray.data.helper.EmployeeQueryCriteria;
import io.john.amiscaray.data.stub.Employee;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryCriteriaTest {

    private final QueryCriteria<Employee, String> queryCriteria = new EmployeeQueryCriteria();

    @Test
    public void testQueryCriteriaCanParseField() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        var testEmployee = new Employee(1L, "Billy", "Tech");

        var employeeName = queryCriteria.parseFieldValueFrom(testEmployee);

        assertEquals("Billy", employeeName);
    }

}
