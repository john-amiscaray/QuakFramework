package io.john.amiscaray.backend.framework.data.query;

import io.john.amiscaray.backend.framework.data.query.numeric.*;
import io.john.amiscaray.backend.framework.data.query.string.ValueContaining;
import io.john.amiscaray.backend.framework.data.query.string.ValueEndsWith;
import io.john.amiscaray.backend.framework.data.query.string.ValueLike;
import io.john.amiscaray.backend.framework.data.query.string.ValueStartsWith;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor
public abstract class BaseQueryCriteria implements QueryCriteria{

    protected final String fieldName;

    public static BaseQueryCriteria valueOfField(String fieldName, Function<String, BaseQueryCriteria> matches) {
        return matches.apply(fieldName);
    }

    public static Function<String, BaseQueryCriteria> is(Object value) {
        return fieldName -> new ValueIs(fieldName, value);
    }

    public static Function<String, BaseQueryCriteria> isOneOf(Object... values) {
        return fieldName -> new ValueIsOneOf(fieldName, values);
    }

    public static Function<String, BaseQueryCriteria> isBetween(Number min, Number max) {
        return fieldName -> new ValueBetween(fieldName, min, max);
    }

    public static Function<String, BaseQueryCriteria> isGreaterThan(Number value) {
        return fieldName -> new ValueGreaterThan(fieldName, value);
    }

    public static Function<String, BaseQueryCriteria> isGreaterThanOrEqualTo(Number value) {
        return fieldName -> new ValueGreaterThanOrEqualTo(fieldName, value);
    }

    public static Function<String, BaseQueryCriteria> isLessThan(Number value) {
        return fieldName -> new ValueLessThan(fieldName, value);
    }

    public static Function<String, BaseQueryCriteria> isLessThanOrEqualTo(Number value) {
        return fieldName -> new ValueLessThanOrEqualTo(fieldName, value);
    }

    public static Function<String, BaseQueryCriteria> contains(String substring) {
        return fieldName -> new ValueContaining(fieldName, substring);
    }

    public static Function<String, BaseQueryCriteria> endsWith(String suffix) {
        return fieldName -> new ValueEndsWith(fieldName, suffix);
    }

    public static Function<String, BaseQueryCriteria> startsWith(String prefix) {
        return fieldName -> new ValueStartsWith(fieldName, prefix);
    }

    public static Function<String, BaseQueryCriteria> isLike(String regex) {
        return fieldName -> new ValueLike(fieldName, regex);
    }

}
 