package io.john.amiscaray.quak.data.query;

import lombok.AllArgsConstructor;

/**
 * Base class for classes implementing {@link io.john.amiscaray.quak.data.query.QueryCriteria}
 */
@AllArgsConstructor
public abstract class SimpleQueryCriteria implements QueryCriteria{

    protected final String fieldName;

}
 