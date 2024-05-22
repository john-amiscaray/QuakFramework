package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import io.john.amiscaray.backend.framework.web.test.stub.MockAccount;
import jakarta.servlet.http.HttpServletResponse;

@Controller(contextPath = "/accounts")
public class MockControllerWithContextPath {

    @Handle(path="/account/{id}")
    public Response<MockAccount> getAccountByID(DynamicPathRequest<Void> request) {

        return new Response<>(HttpServletResponse.SC_OK, new MockAccount(1, 1, 10000, "savings"));

    }

}
