package io.john.amiscaray.backend.framework.data.query.numeric;

import io.john.amiscaray.backend.framework.data.query.SimpleQueryCriteria;

public abstract class NumericQueryCriteria extends SimpleQueryCriteria {

    protected Number[] values;

    public NumericQueryCriteria(String fieldName, Number... values) {
        super(fieldName);
        this.values = values;
    }

}
