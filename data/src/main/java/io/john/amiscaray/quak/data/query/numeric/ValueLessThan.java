package io.john.amiscaray.quak.data.query.numeric;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A NumericQueryCriteria testing that the value of a field is less than a value
 */
public class ValueLessThan extends NumericQueryCriteria{

    public ValueLessThan(String fieldName, Number value) {
        super(fieldName, value);
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.lt(queryRoot.get(fieldName), values[0]);
    }
}
