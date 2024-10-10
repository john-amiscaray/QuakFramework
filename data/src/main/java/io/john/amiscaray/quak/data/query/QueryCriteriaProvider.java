package io.john.amiscaray.quak.data.query;

@FunctionalInterface
public interface QueryCriteriaProvider {

    QueryCriteria provideQueryCriteria(String fieldName);

}
