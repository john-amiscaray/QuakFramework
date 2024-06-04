package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUserAccount;
import lombok.AllArgsConstructor;

import java.util.Date;

@Provider
@AllArgsConstructor
public class MockUserAccountProvider {

    private MockUser user;
    private Date createdOn;
    private Long balance;

    @Provide
    public MockUserAccount userAccount() {
        return new MockUserAccount(user, createdOn, balance);
    }

}
