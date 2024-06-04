package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;
import lombok.AllArgsConstructor;

@Provider
@AllArgsConstructor
public class MockUserProvider {

    private final String username;

    @Provide
    public MockUser getUser() {
        return new MockUser(username);
    }

}
