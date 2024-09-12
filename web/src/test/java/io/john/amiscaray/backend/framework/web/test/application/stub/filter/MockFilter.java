package io.john.amiscaray.backend.framework.web.test.application.stub.filter;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.web.filter.annotation.ApplicationFilter;
import io.john.amiscaray.backend.framework.web.test.util.MockFilterWasCalled;
import jakarta.servlet.*;

import java.io.IOException;

@ApplicationFilter
public class MockFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        var ctx = ApplicationContext.getInstance();
        var future = ctx.getInstance(MockFilterWasCalled.class);
        future.complete(this);
        chain.doFilter(request, response);
    }

}
