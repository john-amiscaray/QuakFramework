package io.john.amiscaray.backend.framework.data.query;

import java.util.Map;

public record NativeQuery(String hql, Map<String, String> params) {

    public NativeQuery(String hql) {
        this(hql, Map.of());
    }

}
