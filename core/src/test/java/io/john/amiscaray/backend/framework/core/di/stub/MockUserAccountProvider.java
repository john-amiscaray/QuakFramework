package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUserAccount;

import java.util.Date;

@Provider
public class MockUserAccountProvider {

    private MockUser user;
    private Date createdOn;
    private Long balance;

    @Provide
    public MockUserAccountProvider(MockUser user, Date createdOn, Long balance) {
        this.user = user;
        this.createdOn = createdOn;
        this.balance = balance;
    }

    @Provide
    public MockUserAccount userAccount() {
        return new MockUserAccount(user, createdOn, balance);
    }

}
