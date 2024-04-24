package io.john.amiscaray.backend.framework.data.update;

public interface FieldUpdate<T> {

    String fieldName();

    Class<T> fieldType();

    UpdateExpression<T> updateExpression();

    static <V> FieldUpdate<V> setFieldToValue(String fieldName, V value, Class<V> fieldType) {
        return new FieldUpdate<>() {
            @Override
            public String fieldName() {
                return fieldName;
            }

            @Override
            public Class<V> fieldType() {
                return fieldType;
            }

            @Override
            public UpdateExpression<V> updateExpression() {
                return UpdateExpression.literal(value);
            }
        };
    }

}
