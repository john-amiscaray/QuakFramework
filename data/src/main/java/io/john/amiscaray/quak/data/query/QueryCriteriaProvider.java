package io.john.amiscaray.quak.data.query;

/**
 * Provides QueryCriteria given a fieldName. Typically used with the {@link io.john.amiscaray.quak.data.query.QueryCriteria#valueOfField valueOfField method} for semantic method chaining. Example:<br>
 * <pre>{@code
 * import static io.john.amiscaray.quak.data.query.QueryCriteria.*;
 *
 * public class Test {
 *
 *     public void test() {
 *         QueryCriteria criteria = valueOfField("name", contains("oh"));
 *     }
 *
 * }}</pre>
 *
 * These are provided using static methods from the {@link io.john.amiscaray.quak.data.query.QueryCriteria QueryCriteria} class.
 */
@FunctionalInterface
public interface QueryCriteriaProvider {

    /**
     * Builds the query criteria
     * @param fieldName The field name
     * @return The QueryCriteria
     */
    QueryCriteria provideQueryCriteria(String fieldName);

}
