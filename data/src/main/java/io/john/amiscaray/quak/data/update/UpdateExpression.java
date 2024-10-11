package io.john.amiscaray.quak.data.update;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import static java.lang.Math.log;

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

    static <N extends Number> UpdateExpression<Double> raiseToThePowerOf(N number) {
        return (currentValue, _queryRoot, cb) -> cb.power(currentValue, number);
    }

    static UpdateExpression<Double> logBaseN(Double n) {
        if (n <= 1) {
            throw new IllegalArgumentException("The base of a logarithm must not be less than or equal to 1.");
        }
        return (currentValue, _queryRoot, cb) -> cb.quot(cb.ln(currentValue), log(n)).as(Double.class);
    }

    static UpdateExpression<Double> ln() {
        return (currentValue, _queryRoot, cb) -> cb.ln(currentValue);
    }

    static UpdateExpression<Double> sqrt() {
        return (currentValue, _queryRoot, cb) -> cb.sqrt(currentValue);
    }

    static UpdateExpression<Number> abs() {
        return (currentValue, _queryRoot, cb) -> cb.abs(currentValue);
    }

    static UpdateExpression<String> prepend(String prefix) {
        return (currentValue, _queryRoot, cb) -> cb.concat(cb.literal(prefix), currentValue);
    }

    static UpdateExpression<String> append(String suffix) {
        return (currentValue, _queryRoot, cb) -> cb.concat(currentValue, cb.literal(suffix));
    }

    static UpdateExpression<String> trim() {
        return (currentValue, _queryRoot, cb) -> cb.trim(currentValue);
    }

    static UpdateExpression<String> trimTrailing() {
        return (currentValue, _queryRoot, cb) -> cb.trim(CriteriaBuilder.Trimspec.TRAILING, currentValue);
    }

    static UpdateExpression<String> trimLeading() {
        return (currentValue, _queryRoot, cb) -> cb.trim(CriteriaBuilder.Trimspec.LEADING, currentValue);
    }

    static UpdateExpression<String> subString(int startIndex) {
        return (currentValue, _queryRoot, cb) -> cb.substring(currentValue, startIndex + 1);
    }

    static UpdateExpression<String> subString(int startIndex, int len) {
        return (currentValue, _queryRoot, cb) -> cb.substring(currentValue, startIndex + 1, len);
    }

}
