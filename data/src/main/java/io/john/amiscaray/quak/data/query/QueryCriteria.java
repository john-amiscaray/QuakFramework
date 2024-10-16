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

/**
 * Used to provide criteria for database querying
 */
public interface QueryCriteria {

    /**
     * Creates a predicate using the given query root and criteria builder.
     * @param queryRoot The query root
     * @param criteriaBuilder The criteria builder
     * @return The predicate representing the criteria to query the database.
     */
    Predicate getTestPredicate(Root<?> queryRoot, CriteriaBuilder criteriaBuilder);

    /**
     * ANDs this query with another one.
     * @param queryCriteria The other query.
     * @return The criteria ANDed with the other criteria
     */
    default QueryCriteria and(QueryCriteria queryCriteria) {
        return new And(this, queryCriteria);
    }

    /**
     * ORs this query with another one.
     * @param queryCriteria The other query.
     * @return A new QueryCriteria representing the criteria ORed with the other criteria
     */
    default QueryCriteria or(QueryCriteria queryCriteria) {
        return new Or(this, queryCriteria);
    }

    /**
     * Creates a query criteria testing that the value of a field matches some condition provided by a query criteria provider.
     * @param fieldName The name of the field.
     * @param queryCriteriaProvider A query criteria provider. This should take the name of the field and test something on it.
     * @return The query about the value of the given field.
     */
    static QueryCriteria valueOfField(String fieldName, QueryCriteriaProvider queryCriteriaProvider) {
        return queryCriteriaProvider.provideQueryCriteria(fieldName);
    }

    /**
     * ANDs the given query criteria providers into a single query criteria provider.
     * @param queryCriteriaProviders The query criteria providers.
     * @return The query criteria providers conjoined into a single one.
     */
    static QueryCriteriaProvider matchesAllOf(QueryCriteriaProvider... queryCriteriaProviders) {
        return fieldName -> new And(Arrays.stream(queryCriteriaProviders)
                .map(queryCriteriaProvider -> queryCriteriaProvider.provideQueryCriteria(fieldName))
                .toList());
    }

    /**
     * Creates a query criteria provider testing that the value of a field equals to the given value.
     * @param value The value.
     * @return A query criteria provider
     */
    static QueryCriteriaProvider is(Object value) {
        return fieldName -> new ValueIs(fieldName, value);
    }

    /**
     * Creates a query criteria provider testing that the value of a field equals one of the given values.
     * @param values The values.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isOneOf(Object... values) {
        return fieldName -> new ValueIsOneOf(fieldName, values);
    }

    /**
     * Creates a query criteria provider testing that the value of a field is between a min and a max.
     * @param min The min
     * @param max The max
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isBetween(Number min, Number max) {
        return fieldName -> new ValueBetween(fieldName, min, max);
    }

    /**
     * Creates a query criteria provider testing that the value of a field is greater than some value.
     * @param value The value.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isGreaterThan(Number value) {
        return fieldName -> new ValueGreaterThan(fieldName, value);
    }

    /**
     * Creates a query criteria provider testing that the value of a field is greater than or equal to some value.
     * @param value The value.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isGreaterThanOrEqualTo(Number value) {
        return fieldName -> new ValueGreaterThanOrEqualTo(fieldName, value);
    }

    /**
     * Creates a query criteria provider testing that the value of a field is less than some value.
     * @param value The value.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isLessThan(Number value) {
        return fieldName -> new ValueLessThan(fieldName, value);
    }

    /**
     * Creates a query criteria provider testing that the value of a field is less than or equal to some value.
     * @param value The value.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isLessThanOrEqualTo(Number value) {
        return fieldName -> new ValueLessThanOrEqualTo(fieldName, value);
    }

    /**
     * Creates a query criteria provider testing that the value of a field contains a substring.
     * @param substring The substring.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider contains(String substring) {
        return fieldName -> new ValueContaining(fieldName, substring);
    }

    /**
     * Creates a query criteria provider testing that the value of a field ends with a suffix.
     * @param suffix The suffix.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider endsWith(String suffix) {
        return fieldName -> new ValueEndsWith(fieldName, suffix);
    }

    /**
     * Creates a query criteria provider testing that the value of a field starts with a prefix.
     * @param prefix The prefix
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider startsWith(String prefix) {
        return fieldName -> new ValueStartsWith(fieldName, prefix);
    }

    /**
     * Creates a query criteria provider testing that the value of a field matches some regex (using SQL syntax).
     * @param regex The regex.
     * @return A query criteria provider.
     */
    static QueryCriteriaProvider isLike(String regex) {
        return fieldName -> new ValueLike(fieldName, regex);
    }

}
