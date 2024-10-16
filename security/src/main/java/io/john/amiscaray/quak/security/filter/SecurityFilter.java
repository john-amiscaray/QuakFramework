package io.john.amiscaray.quak.security.filter;

import jakarta.servlet.Filter;

/**
 * A base class for filters used for security.
 */
public abstract class SecurityFilter implements Filter {

    protected boolean urlMatchesPathPattern(String url, String pattern) {
        if (pattern.endsWith("/*")) {
            String basePattern = pattern.substring(0, pattern.length() - 2);
            return url.startsWith(basePattern);
        } else if (pattern.startsWith("*.")) {
            String extension = pattern.substring(1); // Remove the '*'
            return url.endsWith(extension);
        } else {
            return url.equals(pattern);
        }
    }

}
