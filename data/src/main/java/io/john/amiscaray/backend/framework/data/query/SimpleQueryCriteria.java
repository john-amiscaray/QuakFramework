package io.john.amiscaray.backend.framework.data.query;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public abstract class SimpleQueryCriteria implements QueryCriteria{

    protected final String fieldName;

}
 