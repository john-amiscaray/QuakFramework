package io.john.amiscaray.backend.framework.data.query;

@FunctionalInterface
public interface QueryCriteriaProvider {

    QueryCriteria provideQueryCriteria(String fieldName);

}
