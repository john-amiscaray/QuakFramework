package io.john.amiscaray.backend.framework.security.cors.filter;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CORSFilter implements Filter {

    private SecurityConfig securityConfig;

    @Instantiate
    public CORSFilter(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var origin = httpRequest.getHeader("Origin");
        var allowedOrigins = securityConfig.corsConfig().allowedOrigins();
        var allowedMethods = securityConfig.corsConfig().allowedMethods();
        var allowedHeaders = securityConfig.corsConfig().allowedHeaders();
        var allowAllHeaders = securityConfig.corsConfig().allowAllHeaders();

        if (allowAllHeaders) {
            httpResponse.setHeader("Access-Control-Allow-Headers", String.join(", ", getHeaderNames(httpRequest)));
        } else if (allowedHeaders != null && !allowedHeaders.isEmpty()) {
            httpResponse.setHeader("Access-Control-Allow-Headers", String.join(", ", allowedHeaders));
        }

        if (allowedMethods != null && !allowedMethods.isEmpty()) {
            httpResponse.setHeader("Access-Control-Allow-Methods", String.join(", ", allowedMethods));
        }

        if (allowedOrigins.contains("*")) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        } else if (origin != null && allowedOrigins.contains(origin)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        }

        chain.doFilter(request, response);
    }

    private List<String> getHeaderNames(HttpServletRequest request) {
        var result = new ArrayList<String>();
        var iter = request.getHeaderNames();

        while (iter.hasMoreElements()) {
            result.add(iter.nextElement());
        }

        return result;
    }

}
