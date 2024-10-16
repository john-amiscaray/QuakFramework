package io.john.amiscaray.quak.data.query.numeric;

import io.john.amiscaray.quak.data.query.SimpleQueryCriteria;

/**
 * A QueryCriteria involving numbers
 */
public abstract class NumericQueryCriteria extends SimpleQueryCriteria {

    protected Number[] values;

    public NumericQueryCriteria(String fieldName, Number... values) {
        super(fieldName);
        this.values = values;
    }

}
