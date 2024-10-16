package io.john.amiscaray.quak.data.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Tests that the value of a field equals to some value
 */
public class ValueIs extends SimpleQueryCriteria {

    private final Object value;

    public ValueIs(String fieldName, Object value) {
        super(fieldName);
        this.value = value;
    }
    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return queryRoot.get(fieldName).in(value);
    }

}
