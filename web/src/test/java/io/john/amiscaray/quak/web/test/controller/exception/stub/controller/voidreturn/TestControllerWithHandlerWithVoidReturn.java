package io.john.amiscaray.quak.web.test.controller.exception.stub.controller.voidreturn;

import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.quak.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithVoidReturn {

    @Handle(path= "/void", method = RequestMethod.GET)
    public void invalidReturn(){

    }

}
