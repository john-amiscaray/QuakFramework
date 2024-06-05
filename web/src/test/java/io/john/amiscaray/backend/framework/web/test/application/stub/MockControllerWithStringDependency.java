package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;
import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;

@Controller
public class MockControllerWithStringDependency {

    private final String applicationName;

    @Instantiate
    public MockControllerWithStringDependency(@ProvidedWith(dependencyName="applicationName") String applicationName) {
        this.applicationName = applicationName;
    }

    @Handle(method = RequestMethod.GET, path = "/application")
    public Response<String> getApplicationName(Request<Void> request) {
        return Response.of(applicationName);
    }

}
