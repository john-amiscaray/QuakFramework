package io.john.amiscaray.quak.web.test.controller.exception.stub.controller.noparams;

import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.quak.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithNoParams {

    @Handle(path= "/no/params", method = RequestMethod.GET)
    public String invalidParams(){
        return "Hello";
    }

}
