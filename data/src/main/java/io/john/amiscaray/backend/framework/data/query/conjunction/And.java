package io.john.amiscaray.backend.framework.data.query.conjunction;

import io.john.amiscaray.backend.framework.data.query.QueryCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class And extends Conjunction {

    public And(List<QueryCriteria> conditions) {
        super(conditions);
    }

    public And(QueryCriteria...conditions) {
        super(Arrays.stream(conditions).collect(Collectors.toList()));
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.and(conditions.stream()
                .map(condition -> condition.getTestPredicate(queryRoot, criteriaBuilder))
                .toArray(Predicate[]::new));
    }

}