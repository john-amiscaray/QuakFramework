package io.john.amiscaray.data.update.numeric;

import io.john.amiscaray.data.update.BaseFieldUpdate;
import io.john.amiscaray.data.update.UpdateExpression;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import java.util.function.BinaryOperator;

public abstract class SimpleNumericFieldUpdate<N extends Number> extends BaseFieldUpdate<N> {

    protected final UpdateExpression<N>[] operands;

    @SafeVarargs
    public SimpleNumericFieldUpdate(String fieldName, UpdateExpression<N>... operands) {
        super(fieldName);
        this.operands = operands;
    }

    protected Expression<N> reduceOperandsUsing(BinaryOperator<Expression<N>> accumulator, Root<?> queryRoot, CriteriaBuilder cb) {
        Expression<N> currentExpression = queryRoot.get(fieldName);
        for (var operand : operands) {
            currentExpression = accumulator.apply(currentExpression, operand.createExpression(queryRoot, cb));
        }
        return currentExpression;
    }

}
