package io.john.amiscaray.backend.framework.data.update;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseFieldUpdate<T> implements FieldUpdate<T>{

    protected String fieldName;

}
