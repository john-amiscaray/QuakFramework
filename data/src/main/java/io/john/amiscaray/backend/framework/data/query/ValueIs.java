package io.john.amiscaray.backend.framework.data.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
