package io.john.amiscaray.quak.web.test.application.stub.filter;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.web.filter.annotation.ApplicationFilter;
import io.john.amiscaray.quak.web.test.util.TestFilterCollector;
import jakarta.servlet.*;

import java.io.IOException;

@ApplicationFilter(priority = 2)
public class FilterTwo implements Filter {

    private TestFilterCollector filterCollector;

    @Instantiate
    public FilterTwo(TestFilterCollector filterCollector) {
        this.filterCollector = filterCollector;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        filterCollector.addFilter(this);
        chain.doFilter(request, response);
    }

}