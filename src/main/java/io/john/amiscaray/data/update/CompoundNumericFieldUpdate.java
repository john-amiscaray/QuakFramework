package io.john.amiscaray.data.update;

import io.john.amiscaray.data.update.numeric.SimpleNumericFieldUpdate;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public record CompoundNumericFieldUpdate<N extends Number>(String fieldName, @Singular("apply") List<SimpleNumericFieldUpdate<N>> operations) {
}
