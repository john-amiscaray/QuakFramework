package io.john.amiscaray.data.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public interface QueryCriteria {

    Expression<Boolean> getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder);

}
