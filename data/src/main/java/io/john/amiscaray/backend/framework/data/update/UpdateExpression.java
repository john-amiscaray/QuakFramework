package io.john.amiscaray.backend.framework.data.update;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public interface UpdateExpression<T> {

    Expression<T> apply(Expression<T> currentValue,
                        Root<?> queryRoot,
                        CriteriaBuilder cb);

    static <V> UpdateExpression<V> setTo(V literal) {
        return (_currentValue, _queryRoot, cb) -> cb.literal(literal);
    }

    static <N extends Number> UpdateExpression<N> add(N number) {
        return (currentValue, _queryRoot, cb) -> cb.sum(currentValue, number);
    }

    static <N extends Number> UpdateExpression<N> subtract(N number) {
        return (currentValue, _queryRoot, cb) -> cb.diff(currentValue, number);
    }

    static <N extends Number> UpdateExpression<N> multiply(N number) {
        return (currentValue, _queryRoot, cb) -> cb.prod(currentValue, number);
    }

    static <N extends Number> UpdateExpression<Number> divide(N number) {
        return (currentValue, _queryRoot, cb) -> cb.quot(currentValue, number);
    }

}
