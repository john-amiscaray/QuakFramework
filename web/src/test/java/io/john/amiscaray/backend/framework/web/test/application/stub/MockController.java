package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import static io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo.dummyUser;

@Controller
public class MockController {

    @Handle(path="/", method=RequestMethod.GET)
    public Response<String> helloWorld(Request<Void> request) {

        return Response.of("Hello World");

    }

    @Handle(path="/user", method=RequestMethod.POST)
    public Response<Void> saveUser(Request<MockUserInfo> request) {

        return new Response<>(HttpServletResponse.SC_CREATED, null);

    }

    @Handle(path="/user/age/{age}", method=RequestMethod.GET)
    public Response<List<MockUserInfo>> getUsersWithAge(DynamicPathRequest<Void> request) {

        var age = Integer.parseInt(request.pathVariables().get("age"));
        var body = List.of(dummyUser(age));

        return Response.of(body);

    }

}
