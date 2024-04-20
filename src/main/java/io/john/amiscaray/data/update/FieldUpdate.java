package io.john.amiscaray.data.update;

public interface FieldUpdate<T> {

    String fieldName();

    UpdateExpression<T> updateExpression();

    static <V> FieldUpdate<V> setFieldToValue(String fieldName, V value) {
        return new FieldUpdate<V>() {
            @Override
            public String fieldName() {
                return fieldName;
            }

            @Override
            public UpdateExpression<V> updateExpression() {
                return UpdateExpression.literal(value);
            }
        };
    }

}
