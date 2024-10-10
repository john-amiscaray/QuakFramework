package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.test.di.stub.pojo.MockUser;
import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

@Provider
public class MockUserProvider {

    private final String username;

    @Instantiate
    public MockUserProvider(@ProvidedWith(dependencyName = "username") String username) {
        this.username = username;
    }

    @Provide
    public MockUser getUser() {
        return new MockUser(username);
    }

}
