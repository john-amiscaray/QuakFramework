package io.john.amiscaray.quak.data.query.string;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * A QueryCriteria testing that a string field contains a substring
 */
public class ValueContaining extends StringQueryCriteria{

    public ValueContaining(String fieldName, String value) {
        super(fieldName, value);
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.like(queryRoot.get(fieldName), "%" + value + "%");
    }

}
