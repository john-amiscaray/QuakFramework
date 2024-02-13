package io.john.amiscaray.data.query.string;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class ValueLike extends StringQueryCriteria{
    public ValueLike(String fieldName, String regex) {
        super(fieldName, regex);
    }

    @Override
    public Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(queryRoot.get(fieldName), value);
    }
}
