package io.john.amiscaray.data.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class ValueIsOneOf extends BaseQueryCriteria{
    private final Object[] values;

    public ValueIsOneOf(String fieldName, Object... values) {
        super(fieldName);
        this.values = values;
    }

    @Override
    public Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return queryRoot.get("name").in(values);
    }
}
