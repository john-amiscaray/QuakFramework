package io.john.amiscaray.backend.framework.test.core.di.stub;

import io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockUserAccount;
import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

@Provider
public class MockStringProvider {

    @Provide(dependencyName = "username")
    public String username() {
        return "John";
    }

    @Provide(dependencyName = "accountName")
    public String accountName() {
        return "Savings";
    }

    @Provide(dependencyName = "greeting")
    public String greeting(
            @ProvidedWith(dependencyName = "username") String username,
            @ProvidedWith(dependencyName = "accountName") String accountName,
            @ProvidedWith(dependencyName = "balance") Long balance) {
        return String.format("Hello %s! This is your %s account with balance: $%s", username, accountName, balance);
    }

    @Provide(dependencyName = "accountString")
    public String accountString(MockUserAccount userAccount) {
        return userAccount.toString();
    }

}
