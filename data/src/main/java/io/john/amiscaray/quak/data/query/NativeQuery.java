package io.john.amiscaray.quak.data.query;

import java.util.Map;

/**
 * A wrapper around Hibernate SQL native queries to pass parameters to a query string
 * @param hql The HQL query
 * @param params A map of the parameters and their values for the HQL query
 */
public record NativeQuery(String hql, Map<String, String> params) {

    public NativeQuery(String hql) {
        this(hql, Map.of());
    }

}
