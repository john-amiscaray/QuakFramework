package io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.stringparam;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;

@Controller
public class TestControllerWithHandlerWithStringParam {

    @Handle(path= "/string/param", method = RequestMethod.GET)
    public Response<String> invalidParam(String name){
        return Response.of("Hello " + name);
    }

}
