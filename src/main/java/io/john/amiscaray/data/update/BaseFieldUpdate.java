package io.john.amiscaray.data.update;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class BaseFieldUpdate<T> implements FieldUpdate<T>{

    protected String fieldName;

}
