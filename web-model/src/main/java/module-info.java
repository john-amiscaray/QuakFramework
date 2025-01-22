module quak.framework.web.model {
    exports io.john.amiscaray.quak.http.request;
    exports io.john.amiscaray.quak.http.response;
    exports io.john.amiscaray.quak.http.status;
    requires static lombok;
    requires quak.framework.security;
}