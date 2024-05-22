package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import io.john.amiscaray.backend.framework.web.test.stub.MockAccount;
import jakarta.servlet.http.HttpServletResponse;

import static io.john.amiscaray.backend.framework.web.test.stub.MockAccount.dummyAccount;

@Controller(contextPath = "/accounts")
public class MockControllerWithContextPath {

    @Handle(path="/account/{id}", method = RequestMethod.GET)
    public Response<MockAccount> getAccountByID(DynamicPathRequest<Void> request) {

        var accountID = Integer.parseInt(request.pathVariables().get("id"));
        return new Response<>(HttpServletResponse.SC_OK, dummyAccount(accountID));

    }

}
