package io.john.amiscaray.backend.framework.data.update;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public record FieldUpdate<T>(String fieldName, @Singular("apply") List<UpdateExpression<T>> updates) {



}
