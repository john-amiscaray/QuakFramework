package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;
import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;

@Controller(contextPath = "/application")
public class MockControllerWithDependencies {

    private final String applicationName;
    private final float version;
    private final ApplicationDetails applicationDetails;

    @Instantiate
    public MockControllerWithDependencies(
            @ProvidedWith(dependencyName="applicationName") String applicationName,
            @ProvidedWith(dependencyName="version") float version,
            ApplicationDetails applicationDetails
    ) {
        this.applicationName = applicationName;
        this.version = version;
        this.applicationDetails = applicationDetails;
    }

    @Handle(method = RequestMethod.GET, path = "/name")
    public Response<String> getApplicationName(Request<Void> request) {
        return Response.of(applicationName);
    }

    @Handle(method = RequestMethod.GET, path = "/version")
    public Response<Float> getApplicationVersion(Request<Void> request) {
        return Response.of(version);
    }

    @Handle(method = RequestMethod.GET, path = "/details")
    public Response<ApplicationDetails> getApplicationDetails(Request<Void> request) {
        return Response.of(applicationDetails);
    }

}