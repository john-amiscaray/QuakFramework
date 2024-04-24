package io.john.amiscaray.backend.framework.data.query.numeric;

import io.john.amiscaray.backend.framework.data.query.BaseQueryCriteria;

public abstract class NumericQueryCriteria extends BaseQueryCriteria {

    protected Number[] values;

    public NumericQueryCriteria(String fieldName, Number... values) {
        super(fieldName);
        this.values = values;
    }

}
