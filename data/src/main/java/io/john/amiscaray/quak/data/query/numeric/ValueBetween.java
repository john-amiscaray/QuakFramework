package io.john.amiscaray.quak.data.query.numeric;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A NumericQueryCriteria testing that the value of a field is between a min and a max
 */
public class ValueBetween extends NumericQueryCriteria{

    public ValueBetween(String fieldName, Number min, Number max) {
        super(fieldName, min, max);
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.between(queryRoot.get(fieldName), values[0].doubleValue(), values[1].doubleValue());
    }
}
