package io.john.amiscaray.quak.data.query.numeric;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A NumericQueryCriteria testing that the value of a field is greater than a value
 */
public class ValueGreaterThan extends NumericQueryCriteria{

    public ValueGreaterThan(String fieldName, Number value) {
        super(fieldName, value);
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.gt(queryRoot.get(fieldName), values[0]);
    }
}
