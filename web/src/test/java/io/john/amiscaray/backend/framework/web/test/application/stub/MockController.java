package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MockController {

    @Handle(path="/", method=RequestMethod.GET)
    public Response<String> helloWorld(Request<Void> request) {

        return new Response<>(HttpServletResponse.SC_OK, "Hello World");

    }

    @Handle(path="/user", method=RequestMethod.POST)
    public Response<Void> saveUser(Request<MockUserInfo> request) {

        return new Response<>(HttpServletResponse.SC_CREATED, null);

    }

}
