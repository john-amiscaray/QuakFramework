package io.john.amiscaray.backend.framework.test.core.di.stub;

import io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockUser;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ProvidedWith;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provider;

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
