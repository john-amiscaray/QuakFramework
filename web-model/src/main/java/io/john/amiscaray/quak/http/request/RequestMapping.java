package io.john.amiscaray.quak.http.request;

/**
 * A record used to wrap around an HTTP method and PATH.
 * @param method The HTTP method.
 * @param url The URL path.
 */
public record RequestMapping(RequestMethod method, String url) {

}