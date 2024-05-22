package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;

import java.util.HashMap;

@Controller
public class MockController {

    @Handle(path="/", method=RequestMethod.GET)
    public Response<String> helloWorld(Request<Void> request) {

        return new Response<>(new HashMap<>(), 200, "Hello World");

    }

}
