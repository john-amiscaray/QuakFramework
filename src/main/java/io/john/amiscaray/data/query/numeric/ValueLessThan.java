package io.john.amiscaray.data.query.numeric;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class ValueLessThan extends NumericQueryCriteria{

    public ValueLessThan(String fieldName, Number value) {
        super(fieldName, value);
    }

    @Override
    public Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lt(queryRoot.get(fieldName), values[0]);
    }
}
