package io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.badreturn;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.http.request.Request;
import io.john.amiscaray.backend.framework.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithInvalidReturnType {

    @Handle(path= "/bad/return", method = RequestMethod.GET)
    public String invalidReturn(Request<Void> request){
        return "Hello";
    }

}
