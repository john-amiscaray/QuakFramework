package io.john.amiscaray.quak.data.update;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import static java.lang.Math.log;

/**
 * Represents an operation on a field of a database entity. Used by the database proxy to apply changes to fields of a table row.
 * See the {@link io.john.amiscaray.quak.data.update.FieldUpdate FieldUpdate} class for more info.
 * @param <T> The database entity this update applies to.
 */
@FunctionalInterface
public interface UpdateExpression<T> {

    /**
     * Applies an update to the value of the current field represented by a JPA expression.
     * @param currentValue The JPA expression representing the current value of the field.
     * @param queryRoot The JPA query root.
     * @param cb The JPA criteria builder.
     * @return A JPA expression representing the updated value.
     */
    Expression<T> apply(Expression<T> currentValue,
                        Root<?> queryRoot,
                        CriteriaBuilder cb);

    /**
     * Creates an update expression setting the value of a field to the given value.
     * @param literal The value to set the field to.
     * @return The update expression.
     * @param <V> The type of the value.
     */
    static <V> UpdateExpression<V> setTo(V literal) {
        return (_currentValue, _queryRoot, cb) -> cb.literal(literal);
    }

    /**
     * Creates an update expression adding a number onto the current value of the field
     * @param number The number to add onto the current value.
     * @return The update expression.
     * @param <N> The type of the number.
     */
    static <N extends Number> UpdateExpression<N> add(N number) {
        return (currentValue, _queryRoot, cb) -> cb.sum(currentValue, number);
    }

    /**
     * Creates an update expression subtracting a number onto the current value of the field.
     * @param number The number to add onto the current value.
     * @return The update expression.
     * @param <N> The type of the number.
     */
    static <N extends Number> UpdateExpression<N> subtract(N number) {
        return (currentValue, _queryRoot, cb) -> cb.diff(currentValue, number);
    }

    /**
     * Creates an update expression multiplying the field by the given number.
     * @param number The number to multiply the current value by.
     * @return The update expression.
     * @param <N> The type of the number.
     */
    static <N extends Number> UpdateExpression<N> multiply(N number) {
        return (currentValue, _queryRoot, cb) -> cb.prod(currentValue, number);
    }

    /**
     * Creates an update expression dividing the field by the given number.
     * @param number The number to divide the current value by.
     * @return The update expression.
     * @param <N> The type of the number.
     */
    static <N extends Number> UpdateExpression<Number> divide(N number) {
        return (currentValue, _queryRoot, cb) -> cb.quot(currentValue, number);
    }

    /**
     * Creates an update expression raising the value of a field by the power of the given number.
     * @param number The number.
     * @return The update expression.
     * @param <N> The type of the number.
     */
    static <N extends Number> UpdateExpression<Double> raiseToThePowerOf(N number) {
        return (currentValue, _queryRoot, cb) -> cb.power(currentValue, number);
    }

    /**
     * Creates an update expression evaluating the current value log base n.
     * @param n The number.
     * @return The update expression.
     */
    static UpdateExpression<Double> logBaseN(Double n) {
        if (n <= 1) {
            throw new IllegalArgumentException("The base of a logarithm must not be less than or equal to 1.");
        }
        return (currentValue, _queryRoot, cb) -> cb.quot(cb.ln(currentValue), log(n)).as(Double.class);
    }

    /**
     * Creates an update expression evaluating the natural logarithm of the current value.
     * @return The update expression.
     */
    static UpdateExpression<Double> ln() {
        return (currentValue, _queryRoot, cb) -> cb.ln(currentValue);
    }

    /**
     * Creates an update expression evaluating the square root of the current value.
     * @return The update expression.
     */
    static UpdateExpression<Double> sqrt() {
        return (currentValue, _queryRoot, cb) -> cb.sqrt(currentValue);
    }

    /**
     * Creates an update expression evaluating the absolute value of the current value.
     * @return The update expression.
     */
    static UpdateExpression<Number> abs() {
        return (currentValue, _queryRoot, cb) -> cb.abs(currentValue);
    }

    /**
     * Creates an update expression prepending a string to the current value.
     * @param prefix The prefix to prepend to the current value.
     * @return The update expression.
     */
    static UpdateExpression<String> prepend(String prefix) {
        return (currentValue, _queryRoot, cb) -> cb.concat(cb.literal(prefix), currentValue);
    }

    /**
     * Creates an update expression appending a string to the current value.
     * @param suffix The suffix to append to the current value.
     * @return The update expression.
     */
    static UpdateExpression<String> append(String suffix) {
        return (currentValue, _queryRoot, cb) -> cb.concat(currentValue, cb.literal(suffix));
    }

    /**
     * Creates an update expression trimming the current value.
     * @return The update expression.
     */
    static UpdateExpression<String> trim() {
        return (currentValue, _queryRoot, cb) -> cb.trim(currentValue);
    }

    /**
     * Creates an update expression trimming trailing whitespace from the current value.
     * @return The update expression.
     */
    static UpdateExpression<String> trimTrailing() {
        return (currentValue, _queryRoot, cb) -> cb.trim(CriteriaBuilder.Trimspec.TRAILING, currentValue);
    }

    /**
     * Creates an update expression trimming leading whitespace from the current value.
     * @return The update expression.
     */
    static UpdateExpression<String> trimLeading() {
        return (currentValue, _queryRoot, cb) -> cb.trim(CriteriaBuilder.Trimspec.LEADING, currentValue);
    }

    /**
     * Creates an update expression evaluating a substring of the current value.
     * @param startIndex The start index to substring from.
     * @return The update expression.
     */
    static UpdateExpression<String> subString(int startIndex) {
        return (currentValue, _queryRoot, cb) -> cb.substring(currentValue, startIndex + 1);
    }

    /**
     * Creates an update expression evaluating a substring of the current value.
     * @param startIndex The start index to substring from.
     * @param len The end index to substring from (exclusive).
     * @return The update expression.
     */
    static UpdateExpression<String> subString(int startIndex, int len) {
        return (currentValue, _queryRoot, cb) -> cb.substring(currentValue, startIndex + 1, len);
    }

}
