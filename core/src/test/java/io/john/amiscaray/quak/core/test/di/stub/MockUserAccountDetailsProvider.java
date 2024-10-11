package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

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
