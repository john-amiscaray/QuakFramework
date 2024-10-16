package io.john.amiscaray.quak.data.query.string;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A QueryCriteria testing that a string field matches a given regex (using SQL syntax)
 */
public class ValueLike extends StringQueryCriteria{

    public ValueLike(String fieldName, String regex) {
        super(fieldName, regex);
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(queryRoot.get(fieldName), value);
    }

}
