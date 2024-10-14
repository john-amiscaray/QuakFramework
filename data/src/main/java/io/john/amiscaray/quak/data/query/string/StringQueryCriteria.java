package io.john.amiscaray.quak.data.query.string;

import io.john.amiscaray.quak.data.query.SimpleQueryCriteria;

/**
 * A QueryCriteria involving string fields
 */
public abstract class StringQueryCriteria extends SimpleQueryCriteria {

    protected final String value;

    public StringQueryCriteria(String fieldName, String value) {
        super(fieldName);
        this.value = value;
    }
}
