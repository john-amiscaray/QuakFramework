package io.john.amiscaray.data.helper;

import io.john.amiscaray.data.query.QueryCriteria;
import io.john.amiscaray.data.stub.Employee;

public class EmployeeQueryCriteria implements QueryCriteria<Employee, String> {
    @Override
    public String getFieldName() {
        return "name";
    }
}
