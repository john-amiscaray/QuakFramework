package io.john.amiscaray.data.query.numeric;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class ValueBetween extends NumericQueryCriteria{

    public ValueBetween(String fieldName, Number min, Number max) {
        super(fieldName, min, max);
    }

    @Override
    public Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.between(queryRoot.get(fieldName), values[0].doubleValue(), values[1].doubleValue());
    }
}
