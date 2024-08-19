package io.john.amiscaray.backend.framework.test.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

import java.time.Instant;
import java.util.Date;

@Provider
public class MockUserAccountDetailsProvider {

    @Provide
    public Date createdOn() {
        return Date.from(Instant.ofEpochMilli(1717479300756L));
    }

    @Provide(dependencyName = "balance")
    public Long balance() {
        return 10000L;
    }

}
