package io.john.amiscaray.data.update.numeric;

import io.john.amiscaray.data.update.UpdateExpression;

public class QuotientFieldUpdate extends SimpleNumericFieldUpdate<Number> {
    @SafeVarargs
    public QuotientFieldUpdate(String fieldName, UpdateExpression<Number>... operands) {
        super(fieldName, operands);
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public UpdateExpression<Number> updateExpression() {
        return (queryRoot, cb) -> reduceOperandsUsing(cb::quot, queryRoot, cb);
    }
}
