package io.john.amiscaray.quak.data.query;

/**
 * Provides QueryCriteria given a fieldName
 */
@FunctionalInterface
public interface QueryCriteriaProvider {

    /**
     * Builds the query criteria
     * @param fieldName The field name
     * @return The QueryCriteria
     */
    QueryCriteria provideQueryCriteria(String fieldName);

}
