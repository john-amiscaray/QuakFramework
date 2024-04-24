package io.john.amiscaray.backend.framework.data.query;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseQueryCriteria implements QueryCriteria{

    protected final String fieldName;

}
 