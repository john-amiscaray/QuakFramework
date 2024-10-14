package io.john.amiscaray.quak.data.query.numeric;

import io.john.amiscaray.quak.data.query.QueryCriteria;
import io.john.amiscaray.quak.data.query.ValueIs;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A NumericQueryCriteria testing that the value of a field is greater than or equal to a value
 */
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
