package io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.voidreturn;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.http.request.RequestMethod;

@Controller
public class TestControllerWithHandlerWithVoidReturn {

    @Handle(path= "/void", method = RequestMethod.GET)
    public void invalidReturn(){

    }

}
