package io.john.amiscaray.quak.security.cors.filter;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.security.config.CORSConfig;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import io.john.amiscaray.quak.security.filter.SecurityFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

public class CORSFilter extends SecurityFilter {

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
        var optionalCORSConfig = getApplicableCorsConfig(httpRequest.getRequestURI(), securityConfig);

        if (optionalCORSConfig.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        var corsConfig = optionalCORSConfig.get();
        var allowedOrigins = corsConfig.allowedOrigins();
        var allowedMethods = corsConfig.allowedMethods();
        var allowedHeaders = corsConfig.allowedHeaders();
        var allowAllHeaders = corsConfig.allowAllHeaders();

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

    private Optional<CORSConfig> getApplicableCorsConfig(String url, SecurityConfig securityConfig) {
        if (securityConfig.pathCorsConfigMap().containsKey(url)) {
            return Optional.of(securityConfig.pathCorsConfigMap().get(url));
        }

        CORSConfig result = null;
        var sortedCorsConfigs = new TreeMap<String, CORSConfig>(Comparator.comparingInt(String::length));
        sortedCorsConfigs.putAll(securityConfig.pathCorsConfigMap());

        for (var entry : sortedCorsConfigs.entrySet()) {
            if (urlMatchesPathPattern(url, entry.getKey())) {
                result = entry.getValue();
            }
        }

        return Optional.ofNullable(result);
    }

}
