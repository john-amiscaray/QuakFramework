package io.john.amiscaray.backend.framework.security.test.cors.filter;

import io.john.amiscaray.backend.framework.security.config.CORSConfig;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import io.john.amiscaray.backend.framework.security.cors.filter.CORSFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class CORSFilterTest {

    private static SecurityConfig securityConfigAllowingOneOriginAndGETAndPOSTMethods() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig("/*", CORSConfig.builder()
                        .allowedOrigins(List.of("https://mysite.org"))
                        .allowedMethods(List.of("GET", "POST"))
                        .allowAllHeaders(true)
                        .build())
                .build();
    }

    private static SecurityConfig securityConfigAllowingMultipleOriginsAndGETAndPOSTMethods() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig("/*", CORSConfig.builder()
                        .allowedOrigins(List.of("https://mysite.org", "https://myothersite.com"))
                        .allowedMethods(List.of("GET", "POST"))
                        .allowAllHeaders(true)
                        .build())
                .build();
    }

    private static SecurityConfig securityConfigAllowingOnlyThreeHeadersAndGETAndPOSTMethods() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig("/*", CORSConfig.builder()
                        .allowedOrigins(List.of("https://mysite.org"))
                        .allowedMethods(List.of("GET", "POST"))
                        .allowHeader("Authorization")
                        .allowHeader("Origin")
                        .allowHeader("Accept")
                        .build())
                .build();
    }

    private static SecurityConfig securityConfigConfiguringCORSForOnlyOnePath() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig("/student/john", CORSConfig.builder()
                        .allowedOrigins(List.of("https://mysite.org"))
                        .allowedMethods(List.of("GET", "POST"))
                        .allowHeader("Authorization")
                        .allowHeader("Origin")
                        .allowHeader("Accept")
                        .build())
                .build();
    }

    private static SecurityConfig securityConfigConfiguringCORSForAllPathsAndOneSpecificPath() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig("/*", CORSConfig.builder()
                        .allowOrigin("https://someothersite.ca")
                        .allowMethod("GET")
                        .allowHeader("Authorization")
                        .build())
                .securePathWithCorsConfig("/student/john", CORSConfig.builder()
                        .allowedOrigins(List.of("https://mysite.org"))
                        .allowedMethods(List.of("GET", "POST"))
                        .allowAllHeaders(true)
                        .build())
                .build();
    }

    private static SecurityConfig securityConfigAllowingAllOrigins() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig("/*", CORSConfig.allowAll())
                .build();
    }

    @Test
    public void testCORSFilterAddsAllowOriginHeaderToRequestAndContinuesChain() throws ServletException, IOException {
        var filter = new CORSFilter(securityConfigAllowingOneOriginAndGETAndPOSTMethods());
        var mockRequest = mock(HttpServletRequest.class);
        var mockResponse = mock(HttpServletResponse.class);
        var mockFilterChain = mock(FilterChain.class);
        when(mockRequest.getHeader("Origin")).thenReturn("https://mysite.org");
        when(mockRequest.getRequestURI()).thenReturn("/");
        when(mockRequest.getHeader("Access-Control-Request-Method")).thenReturn("GET");
        when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Authorization", "Origin")));

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Origin", "https://mysite.org");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Methods", "GET, POST");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Headers", "Authorization, Origin");
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testCORSFilterSupportsMultipleAllowedOriginsAndPutsAllowedOriginAsCurrentOrigin() throws ServletException, IOException {
        var filter = new CORSFilter(securityConfigAllowingMultipleOriginsAndGETAndPOSTMethods());
        var mockRequest = mock(HttpServletRequest.class);
        var mockResponse = mock(HttpServletResponse.class);
        var mockFilterChain = mock(FilterChain.class);
        when(mockRequest.getHeader("Origin")).thenReturn("https://myothersite.com");
        when(mockRequest.getRequestURI()).thenReturn("/");
        when(mockRequest.getHeader("Access-Control-Request-Method")).thenReturn("GET");
        when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Authorization", "Origin")));

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Origin", "https://myothersite.com");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Methods", "GET, POST");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Headers", "Authorization, Origin");
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testCORSFilterConfiguredForSpecificHeadersReturnsAllowHeadersHeaderWithConfiguredHeaders() throws ServletException, IOException {
        var filter = new CORSFilter(securityConfigAllowingOnlyThreeHeadersAndGETAndPOSTMethods());
        var mockRequest = mock(HttpServletRequest.class);
        var mockResponse = mock(HttpServletResponse.class);
        var mockFilterChain = mock(FilterChain.class);
        when(mockRequest.getHeader("Origin")).thenReturn("https://mysite.org");
        when(mockRequest.getRequestURI()).thenReturn("/");
        when(mockRequest.getHeader("Access-Control-Request-Method")).thenReturn("GET");
        when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Authorization", "Origin")));

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Headers", "Authorization, Origin, Accept");
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testCORSFilterConfiguredToAllowAllRequestsAllowsEveryMethodOriginAndHeader() throws ServletException, IOException {
        var filter = new CORSFilter(securityConfigAllowingAllOrigins());
        var mockRequest = mock(HttpServletRequest.class);
        var mockResponse = mock(HttpServletResponse.class);
        var mockFilterChain = mock(FilterChain.class);
        when(mockRequest.getHeader("Origin")).thenReturn("https://mysite.org");
        when(mockRequest.getRequestURI()).thenReturn("/");
        when(mockRequest.getHeader("Access-Control-Request-Method")).thenReturn("GET");
        when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Authorization", "Origin", "Accept", "Accept-Language")));

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Origin", "*");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS, HEAD, CONNECT, TRACE");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Headers", "Authorization, Origin, Accept, Accept-Language");
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testCORSFilterSkipsRequestForPathWithoutCORSConfig() throws ServletException, IOException {
        var filter = new CORSFilter(securityConfigConfiguringCORSForOnlyOnePath());
        var mockRequest = mock(HttpServletRequest.class);
        var mockResponse = mock(HttpServletResponse.class);
        var mockFilterChain = mock(FilterChain.class);
        when(mockRequest.getHeader("Origin")).thenReturn("https://mysite.org");
        when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of()));
        when(mockRequest.getRequestURI()).thenReturn("/");

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(0)).setHeader(anyString(), anyString());
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    public void testCORSFilterSelectsMoreSpecificPathForCORSConfigWithWildCardPathAndOneSpecificPath() throws ServletException, IOException {
        var filter = new CORSFilter(securityConfigConfiguringCORSForAllPathsAndOneSpecificPath());
        var mockRequest = mock(HttpServletRequest.class);
        var mockResponse = mock(HttpServletResponse.class);
        var mockFilterChain = mock(FilterChain.class);
        when(mockRequest.getHeader("Origin")).thenReturn("https://mysite.org");
        when(mockRequest.getHeaderNames()).thenReturn(Collections.enumeration(List.of("Authorization", "Origin", "Accept", "Accept-Language")));
        when(mockRequest.getRequestURI()).thenReturn("/student/john");

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Origin", "https://mysite.org");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Methods", "GET, POST");
        verify(mockResponse, times(1)).setHeader("Access-Control-Allow-Headers", "Authorization, Origin, Accept, Accept-Language");
        verify(mockFilterChain, times(1)).doFilter(mockRequest, mockResponse);
    }

}
