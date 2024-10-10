package io.john.amiscaray.quak.web.test.application.stub.controller;

import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.quak.web.test.stub.MockAccount;
import io.john.amiscaray.quak.http.request.DynamicPathRequest;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.request.RequestMethod;

import java.util.List;

import static io.john.amiscaray.quak.web.test.stub.MockAccount.*;

@Controller(contextPath = "/accounts")
public class MockControllerWithContextPath {

    @Handle(path="/account/{id}", method = RequestMethod.GET)
    public Response<MockAccount> getAccountByID(DynamicPathRequest<Void> request) {

        var accountID = Integer.parseInt(request.pathVariables().get("id"));
        return Response.of(dummyAccount(accountID));

    }

    @Handle(path="/account", method = RequestMethod.GET)
    public Response<List<MockAccount>> getAccountsWithQueryCriteria(Request<Void> request) {

        var accountName = request.queryParams().get("name");
        var balance = request.queryParams().get("balance");
        if (accountName != null && balance != null) {
            return Response.of(dummyAccountsWithNameAndUserBalance(accountName, Long.parseLong(balance)));
        } else if (accountName != null) {
            return Response.of(dummyAccountsWithName(accountName));
        }

        return Response.of(List.of());

    }

}
