package io.john.amiscaray.backend.framework.data.query.string;

import io.john.amiscaray.backend.framework.data.query.SimpleQueryCriteria;

public abstract class StringQueryCriteria extends SimpleQueryCriteria {
    protected final String value;
    public StringQueryCriteria(String fieldName, String value) {
        super(fieldName);
        this.value = value;
    }
}
