package io.john.amiscaray.data.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class ValueIs extends BaseQueryCriteria{
    private final Object value;
    public ValueIs(String fieldName, Object value) {
        super(fieldName);
        this.value = value;
    }
    @Override
    public Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return queryRoot.get("name").in(value);
    }
}
