package io.john.amiscaray.backend.framework.data.query.numeric;

import io.john.amiscaray.backend.framework.data.query.QueryCriteria;
import io.john.amiscaray.backend.framework.data.query.ValueIs;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ValueGreaterThanOrEqualTo extends NumericQueryCriteria{

    private final QueryCriteria greaterThanOrEqualToConjunction;

    public ValueGreaterThanOrEqualTo(String fieldName, Number value) {
        super(fieldName, value);
        greaterThanOrEqualToConjunction = new ValueGreaterThan(fieldName, value).or(new ValueIs(fieldName, value));
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return greaterThanOrEqualToConjunction.getTestPredicate(queryRoot, criteriaBuilder);
    }

}
