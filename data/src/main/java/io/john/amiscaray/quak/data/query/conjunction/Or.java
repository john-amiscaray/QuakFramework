package io.john.amiscaray.quak.data.query.conjunction;

import io.john.amiscaray.quak.data.query.QueryCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to OR two query criteria
 */
public class Or extends Conjunction {

    public Or(List<QueryCriteria> conditions) {
        super(conditions);
    }

    public Or(QueryCriteria...conditions) {
        super(Arrays.stream(conditions).collect(Collectors.toList()));
    }

    @Override
    public Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.or(conditions.stream()
                .map(condition -> condition.getTestPredicate(queryRoot, criteriaBuilder))
                .toArray(Predicate[]::new));
    }

}
