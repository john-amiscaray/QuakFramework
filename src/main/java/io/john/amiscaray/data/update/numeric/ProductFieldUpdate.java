package io.john.amiscaray.data.update.numeric;

import io.john.amiscaray.data.update.UpdateExpression;

public class ProductFieldUpdate<N extends Number> extends SimpleNumericFieldUpdate<N> {
    @SafeVarargs
    public ProductFieldUpdate(String fieldName, UpdateExpression<N>... operands) {
        super(fieldName, operands);
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public UpdateExpression<N> updateExpression() {
        return (queryRoot, cb) -> reduceOperandsUsing(cb::prod, queryRoot, cb);
    }
}
