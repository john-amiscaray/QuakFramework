package io.john.amiscaray.backend.framework.data.query.string;

import io.john.amiscaray.backend.framework.data.query.BaseQueryCriteria;

public abstract class StringQueryCriteria extends BaseQueryCriteria {
    protected final String value;
    public StringQueryCriteria(String fieldName, String value) {
        super(fieldName);
        this.value = value;
    }
}
