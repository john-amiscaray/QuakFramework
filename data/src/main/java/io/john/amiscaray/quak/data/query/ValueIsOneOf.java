package io.john.amiscaray.quak.data.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Tests that the value of a field is equal to one of the values in a set of values
 */
public class ValueIsOneOf extends SimpleQueryCriteria {

    private final Object[] values;

    public ValueIsOneOf(String fieldName, Object... values) {
        super(fieldName);
        this.values = values;
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return queryRoot.get("name").in(values);
    }

}
