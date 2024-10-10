package io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.noparams;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithNoParams {

    @Handle(path= "/no/params", method = RequestMethod.GET)
    public String invalidParams(){
        return "Hello";
    }

}
