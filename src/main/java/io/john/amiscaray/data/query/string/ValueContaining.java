package io.john.amiscaray.data.query.string;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class ValueContaining extends StringQueryCriteria{
    public ValueContaining(String fieldName, String value) {
        super(fieldName, value);
    }

    @Override
    public Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(queryRoot.get(fieldName), "%" + value + "%");
    }
}