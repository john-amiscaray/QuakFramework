package io.john.amiscaray.quak.web.test.controller.exception.stub.controller.badreturn;

import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithInvalidReturnType {

    @Handle(path= "/bad/return", method = RequestMethod.GET)
    public String invalidReturn(Request<Void> request){
        return "Hello";
    }

}
