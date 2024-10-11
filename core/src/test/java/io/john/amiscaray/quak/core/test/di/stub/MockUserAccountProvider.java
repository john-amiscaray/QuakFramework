package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.test.di.stub.pojo.MockUser;
import io.john.amiscaray.quak.core.test.di.stub.pojo.MockUserAccount;
import io.john.amiscaray.quak.core.di.provider.annotation.*;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Provider
@EqualsAndHashCode
public class MockUserAccountProvider {

    private MockUser user;
    private Date createdOn;
    private Long balance;
    private String accountName;

    @Instantiate
    @AggregateTo(aggregateList = "userAccounts")
    public MockUserAccountProvider(MockUser user,
                                   Date createdOn,
                                   @ProvidedWith(dependencyName = "balance") Long balance,
                                   @ProvidedWith(dependencyName = "accountName")
                                   String accountName) {
        this.user = user;
        this.createdOn = createdOn;
        this.balance = balance;
        this.accountName = accountName;
    }

    @Provide
    @AggregateTo(aggregateList = "userAccounts")
    public MockUserAccount userAccount() {
        return new MockUserAccount(user, createdOn, balance, accountName);
    }

}
