package io.john.amiscaray.quak.web.test.application.stub.filter;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.web.filter.annotation.ApplicationFilter;
import io.john.amiscaray.quak.web.test.util.TestFilterCollector;
import jakarta.servlet.*;

import java.io.IOException;

@ApplicationFilter(urlPatterns = { "/users", "/user/*" })
public class UsersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var ctx = ApplicationContext.getInstance();
        var filterCollector = ctx.getInstance(TestFilterCollector.class);

        filterCollector.addFilter(this);

        chain.doFilter(request, response);
    }

}
