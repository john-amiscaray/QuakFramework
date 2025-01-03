package io.john.amiscaray.quak.http.request;

/**
 * A record used to hold an HTTP method and path.
 * @param method The HTTP method.
 * @param url The URL path.
 */
public record RequestMapping(RequestMethod method, String url) {

}