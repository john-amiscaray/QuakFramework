package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;

@Provider
public class MockUserProvider {

    private final String username;

    @Provide
    public MockUserProvider(@ProvidedWith(dependencyName = "username") String username) {
        this.username = username;
    }

    @Provide
    public MockUser getUser() {
        return new MockUser(username);
    }

}
