package io.john.amiscaray.backend.framework.data.update;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public interface UpdateExpression<T> {

    Expression<T> createExpression(Root<?> queryRoot, CriteriaBuilder cb);

    static <V> UpdateExpression<V> literal(V literal) {
        return (_queryRoot, cb) -> cb.literal(literal);
    }

}
