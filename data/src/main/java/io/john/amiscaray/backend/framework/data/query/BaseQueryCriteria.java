package io.john.amiscaray.backend.framework.data.query;

import io.john.amiscaray.backend.framework.data.query.conjunction.And;
import io.john.amiscaray.backend.framework.data.query.numeric.*;
import io.john.amiscaray.backend.framework.data.query.string.ValueContaining;
import io.john.amiscaray.backend.framework.data.query.string.ValueEndsWith;
import io.john.amiscaray.backend.framework.data.query.string.ValueLike;
import io.john.amiscaray.backend.framework.data.query.string.ValueStartsWith;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public abstract class BaseQueryCriteria implements QueryCriteria{

    protected final String fieldName;

    public static QueryCriteria valueOfField(String fieldName, QueryCriteriaProvider queryCriteriaProvider) {
        return queryCriteriaProvider.provideQueryCriteria(fieldName);
    }

    public static QueryCriteriaProvider matchesAllOf(QueryCriteriaProvider... queryCriteriaProviders) {
        return fieldName -> new And(Arrays.stream(queryCriteriaProviders)
                .map(queryCriteriaProvider -> queryCriteriaProvider.provideQueryCriteria(fieldName))
                .toList());
    }

    public static QueryCriteriaProvider is(Object value) {
        return fieldName -> new ValueIs(fieldName, value);
    }

    public static QueryCriteriaProvider isOneOf(Object... values) {
        return fieldName -> new ValueIsOneOf(fieldName, values);
    }

    public static QueryCriteriaProvider isBetween(Number min, Number max) {
        return fieldName -> new ValueBetween(fieldName, min, max);
    }

    public static QueryCriteriaProvider isGreaterThan(Number value) {
        return fieldName -> new ValueGreaterThan(fieldName, value);
    }

    public static QueryCriteriaProvider isGreaterThanOrEqualTo(Number value) {
        return fieldName -> new ValueGreaterThanOrEqualTo(fieldName, value);
    }

    public static QueryCriteriaProvider isLessThan(Number value) {
        return fieldName -> new ValueLessThan(fieldName, value);
    }

    public static QueryCriteriaProvider isLessThanOrEqualTo(Number value) {
        return fieldName -> new ValueLessThanOrEqualTo(fieldName, value);
    }

    public static QueryCriteriaProvider contains(String substring) {
        return fieldName -> new ValueContaining(fieldName, substring);
    }

    public static QueryCriteriaProvider endsWith(String suffix) {
        return fieldName -> new ValueEndsWith(fieldName, suffix);
    }

    public static QueryCriteriaProvider startsWith(String prefix) {
        return fieldName -> new ValueStartsWith(fieldName, prefix);
    }

    public static QueryCriteriaProvider isLike(String regex) {
        return fieldName -> new ValueLike(fieldName, regex);
    }

}
 