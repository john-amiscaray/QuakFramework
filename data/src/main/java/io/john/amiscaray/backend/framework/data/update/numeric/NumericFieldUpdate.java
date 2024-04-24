package io.john.amiscaray.backend.framework.data.update.numeric;

import io.john.amiscaray.backend.framework.data.update.BaseFieldUpdate;
import io.john.amiscaray.backend.framework.data.update.UpdateExpression;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.function.BinaryOperator;

public abstract class NumericFieldUpdate<N extends Number> extends BaseFieldUpdate<N> {

    protected final UpdateExpression<Number>[] operands;

    protected final Class<N> fieldType;

    @SafeVarargs
    public NumericFieldUpdate(String fieldName, Class<N> fieldType, UpdateExpression<Number>... operands) {
        super(fieldName);
        this.operands = operands;
        this.fieldType = fieldType;
    }

    protected Expression<N> reduceOperandsUsing(BinaryOperator<Expression<? extends Number>> accumulator, Root<?> queryRoot, CriteriaBuilder cb) {
        Expression<N> currentExpression = queryRoot.get(fieldName);
        for (var operand : operands) {
            currentExpression = accumulator.apply(currentExpression, operand.createExpression(queryRoot, cb)).as(fieldType);
        }
        return currentExpression;
    }

}
