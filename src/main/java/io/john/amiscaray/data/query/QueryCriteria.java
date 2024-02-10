package io.john.amiscaray.data.query;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public interface QueryCriteria <T, FT> {
    // TODO make this a sealed interface with the different types of queries (number between, value is, etc). Make Helper default method to get an instance of each type and add to result list?
    String getFieldName();

    default FT parseFieldValueFrom(T entity) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        // ((ParameterizedTypeImpl) getClass().getGenericInterfaces()[0]).getActualTypeArguments()
        var queryCriteriaInterface = Arrays.stream(getClass().getGenericInterfaces())
                .filter(type -> type instanceof ParameterizedType parameterizedType && parameterizedType.getRawType().getTypeName().equals(QueryCriteria.class.getTypeName()))
                .findFirst().orElseThrow();
        Class<FT> fieldType;
        if (queryCriteriaInterface instanceof ParameterizedType parameterizedType) {
            fieldType = (Class<FT>) Class.forName(parameterizedType.getActualTypeArguments()[1].getTypeName());
        } else {
            throw new RuntimeException("Unable to parse type of field");
        }
        var queriedField = entity.getClass().getDeclaredField(getFieldName());
        queriedField.setAccessible(true);
        if (!queriedField.getType().equals(fieldType)) {
            throw new NoSuchFieldException("No field named " + getFieldName() + " exists with type: " + fieldType.getSimpleName());
        }
        return (FT) queriedField.get(entity);
    }

}
