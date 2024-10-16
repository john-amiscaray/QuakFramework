package io.john.amiscaray.quak.data.query.string;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A QueryCriteria testing that a string field ends with a suffix
 */
public class ValueEndsWith extends StringQueryCriteria{

    public ValueEndsWith(String fieldName, String value) {
        super(fieldName, value);
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(queryRoot.get(fieldName), "%" + value);
    }
}
