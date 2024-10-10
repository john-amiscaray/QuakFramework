module backend.framework.web.model {
    exports io.john.amiscaray.backend.framework.http.request;
    exports io.john.amiscaray.backend.framework.http.response;
    requires static lombok;
    requires backend.framework.security;
}