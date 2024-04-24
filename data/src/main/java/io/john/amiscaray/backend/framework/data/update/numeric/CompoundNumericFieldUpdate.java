package io.john.amiscaray.backend.framework.data.update.numeric;

import io.john.amiscaray.backend.framework.data.update.UpdateExpression;
import jakarta.persistence.criteria.Expression;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

import static io.john.amiscaray.backend.framework.data.update.numeric.CompoundNumericFieldUpdate.SubOperationType.*;

public class CompoundNumericFieldUpdate<N extends Number> extends NumericFieldUpdate<N> {

    private String fieldName;
    private List<SubOperation<Number>> subOperations;

    @Builder
    public CompoundNumericFieldUpdate(String fieldName, Class<N> fieldType, @Singular("apply") List<SubOperation<Number>> subOperations) {
        super(fieldName, fieldType);
        this.fieldName = fieldName;
        this.subOperations = subOperations;
    }

    public static <N extends Number> CompoundNumericFieldUpdateBuilder<N> builder() {
        return new CompoundNumericFieldUpdateBuilder<N>();
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
        return (queryRoot, cb) -> {
            Expression<Number> currentExpression = queryRoot.get(fieldName);
            for (var subOperation : subOperations) {
                switch (subOperation) {
                    case SubOperation(var type, var operand) when type.equals(SUM) -> currentExpression = cb.sum(currentExpression, operand);
                    case SubOperation(var type, var operand) when type.equals(MINUS) -> currentExpression = cb.diff(currentExpression, operand);
                    case SubOperation(var type, var operand) when type.equals(PROD) -> currentExpression = cb.prod(currentExpression, operand);
                    case SubOperation(var type, var operand) when type.equals(DIV) -> currentExpression = cb.quot(currentExpression, operand);
                    default -> throw new IllegalStateException("Unexpected value: " + subOperation);
                }
            }
            return currentExpression.as(fieldType);
        };
    }

    public record SubOperation<N extends Number>(SubOperationType type, N operand) {
    }

    public enum SubOperationType {
        SUM,
        MINUS,
        PROD,
        DIV
    }

}
