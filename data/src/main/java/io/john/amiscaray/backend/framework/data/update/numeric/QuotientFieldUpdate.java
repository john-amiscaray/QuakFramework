package io.john.amiscaray.backend.framework.data.update.numeric;

import io.john.amiscaray.backend.framework.data.update.UpdateExpression;

public class QuotientFieldUpdate<N extends Number> extends NumericFieldUpdate<N> {
    @SafeVarargs
    public QuotientFieldUpdate(String fieldName, Class<N> fieldType, UpdateExpression<Number>... operands) {
        super(fieldName, fieldType, operands);
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public Class<N> fieldType() {
        return fieldType;
    }

    @Override
    public UpdateExpression<N> updateExpression() {
        return (queryRoot, cb) -> reduceOperandsUsing(cb::quot, queryRoot, cb);
    }
}