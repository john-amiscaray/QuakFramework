package io.john.amiscaray.backend.framework.web.test.application.stub.filter;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.web.filter.annotation.ApplicationFilter;
import io.john.amiscaray.backend.framework.web.test.util.TestFilterCollector;
import jakarta.servlet.*;

import java.io.IOException;

@ApplicationFilter(priority = 2)
public class FilterTwo implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var ctx = ApplicationContext.getInstance();
        var filterCollector = ctx.getInstance(TestFilterCollector.class);

        filterCollector.addFilter(this);
        chain.doFilter(request, response);
    }

}
