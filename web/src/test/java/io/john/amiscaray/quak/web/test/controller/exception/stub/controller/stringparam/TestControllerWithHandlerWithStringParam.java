package io.john.amiscaray.quak.web.test.controller.exception.stub.controller.stringparam;

import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.quak.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithStringParam {

    @Handle(path= "/string/param", method = RequestMethod.GET)
    public Response<String> invalidParam(String name){
        return Response.of("Hello " + name);
    }

}
