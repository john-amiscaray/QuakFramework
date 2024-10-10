package io.john.amiscaray.quak.data.query.numeric;

import io.john.amiscaray.quak.data.query.QueryCriteria;
import io.john.amiscaray.quak.data.query.ValueIs;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ValueLessThanOrEqualTo extends NumericQueryCriteria{

    private final QueryCriteria lessThanOrEqualToConjunction;

    public ValueLessThanOrEqualTo(String fieldName, Number value) {
        super(fieldName, value);
        lessThanOrEqualToConjunction = new ValueLessThan(fieldName, value).or(new ValueIs(fieldName, value));
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return lessThanOrEqualToConjunction.getTestPredicate(queryRoot, criteriaBuilder);
    }

}
