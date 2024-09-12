package io.john.amiscaray.backend.framework.web.test.application.stub.filter;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.backend.framework.web.filter.annotation.ApplicationFilter;
import io.john.amiscaray.backend.framework.web.test.util.TestFilterCollector;
import jakarta.servlet.*;

import java.io.IOException;

@ApplicationFilter(priority = 1)
public class FilterOne implements Filter {

    private TestFilterCollector filterCollector;

    @Instantiate
    public FilterOne(TestFilterCollector filterCollector) {
        this.filterCollector = filterCollector;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        filterCollector.addFilter(this);
        chain.doFilter(request, response);
    }

}
