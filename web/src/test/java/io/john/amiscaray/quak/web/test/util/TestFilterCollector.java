package io.john.amiscaray.quak.web.test.util;

import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import jakarta.servlet.Filter;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@ManagedType
public class TestFilterCollector {

    private final List<Filter> appliedFilters = new ArrayList<>();

    public void addFilter(Filter filter) {
        appliedFilters.add(filter);
    }

    public void clear() {
        appliedFilters.clear();
    }

}
