package io.john.amiscaray.backend.framework.data.query;

@FunctionalInterface
public interface QueryCriteriaProvider {

    BaseQueryCriteria provideQueryCriteriaGivenFieldName(String fieldName);

}
