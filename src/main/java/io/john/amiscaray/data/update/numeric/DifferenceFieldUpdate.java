package io.john.amiscaray.data.update.numeric;

import io.john.amiscaray.data.update.UpdateExpression;

public class DifferenceFieldUpdate<N extends Number> extends NumericFieldUpdate<N> {

    @SafeVarargs
    public DifferenceFieldUpdate(String fieldName, Class<N> fieldType, UpdateExpression<N>... operands) {
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
        return (queryRoot, cb) -> reduceOperandsUsing(cb::diff, queryRoot, cb);
    }

}
