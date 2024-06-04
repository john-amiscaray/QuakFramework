package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUserAccount;

import java.util.Date;

@Provider
public class MockUserAccountProvider {

    private MockUser user;
    private Date createdOn;
    private Long balance;
    private String accountName;

    @Provide
    public MockUserAccountProvider(MockUser user,
                                   Date createdOn,
                                   Long balance,
                                   @ProvidedWith(dependencyName = "accountName")
                                   String accountName) {
        this.user = user;
        this.createdOn = createdOn;
        this.balance = balance;
        this.accountName = accountName;
    }

    @Provide
    public MockUserAccount userAccount() {
        return new MockUserAccount(user, createdOn, balance, accountName);
    }

}
