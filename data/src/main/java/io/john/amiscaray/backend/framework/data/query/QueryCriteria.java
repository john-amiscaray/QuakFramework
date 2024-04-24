package io.john.amiscaray.backend.framework.data.query;

import io.john.amiscaray.backend.framework.data.query.conjunction.And;
import io.john.amiscaray.backend.framework.data.query.conjunction.Or;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface QueryCriteria {

    Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder);

    default QueryCriteria and(QueryCriteria queryCriteria) {
        return new And(this, queryCriteria);
    }

    default QueryCriteria or(QueryCriteria queryCriteria) {
        return new Or(this, queryCriteria);
    }

}
