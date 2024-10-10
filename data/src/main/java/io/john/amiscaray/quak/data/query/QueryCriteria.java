package io.john.amiscaray.quak.data.query;

import io.john.amiscaray.quak.data.query.conjunction.And;
import io.john.amiscaray.quak.data.query.conjunction.Or;
import io.john.amiscaray.quak.data.query.numeric.*;
import io.john.amiscaray.quak.data.query.string.ValueContaining;
import io.john.amiscaray.quak.data.query.string.ValueEndsWith;
import io.john.amiscaray.quak.data.query.string.ValueLike;
import io.john.amiscaray.quak.data.query.string.ValueStartsWith;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Arrays;

public interface QueryCriteria {

    Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder);

    default QueryCriteria and(QueryCriteria queryCriteria) {
        return new And(this, queryCriteria);
    }

    default QueryCriteria or(QueryCriteria queryCriteria) {
        return new Or(this, queryCriteria);
    }

    static QueryCriteria valueOfField(String fieldName, QueryCriteriaProvider queryCriteriaProvider) {
        return queryCriteriaProvider.provideQueryCriteria(fieldName);
    }

    static QueryCriteriaProvider matchesAllOf(QueryCriteriaProvider... queryCriteriaProviders) {
        return fieldName -> new And(Arrays.stream(queryCriteriaProviders)
                .map(queryCriteriaProvider -> queryCriteriaProvider.provideQueryCriteria(fieldName))
                .toList());
    }

    static QueryCriteriaProvider is(Object value) {
        return fieldName -> new ValueIs(fieldName, value);
    }

    static QueryCriteriaProvider isOneOf(Object... values) {
        return fieldName -> new ValueIsOneOf(fieldName, values);
    }

    static QueryCriteriaProvider isBetween(Number min, Number max) {
        return fieldName -> new ValueBetween(fieldName, min, max);
    }

    static QueryCriteriaProvider isGreaterThan(Number value) {
        return fieldName -> new ValueGreaterThan(fieldName, value);
    }

    static QueryCriteriaProvider isGreaterThanOrEqualTo(Number value) {
        return fieldName -> new ValueGreaterThanOrEqualTo(fieldName, value);
    }

    static QueryCriteriaProvider isLessThan(Number value) {
        return fieldName -> new ValueLessThan(fieldName, value);
    }

    static QueryCriteriaProvider isLessThanOrEqualTo(Number value) {
        return fieldName -> new ValueLessThanOrEqualTo(fieldName, value);
    }

    static QueryCriteriaProvider contains(String substring) {
        return fieldName -> new ValueContaining(fieldName, substring);
    }

    static QueryCriteriaProvider endsWith(String suffix) {
        return fieldName -> new ValueEndsWith(fieldName, suffix);
    }

    static QueryCriteriaProvider startsWith(String prefix) {
        return fieldName -> new ValueStartsWith(fieldName, prefix);
    }

    static QueryCriteriaProvider isLike(String regex) {
        return fieldName -> new ValueLike(fieldName, regex);
    }

}
