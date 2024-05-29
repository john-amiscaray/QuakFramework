package io.john.amiscaray.backend.framework.web.test.stub;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MockAccount {

    private int id;
    private int userID;
    private long userBalance;
    private String name;

    public static MockAccount dummyAccount() {
        return dummyAccount(1);
    }

    public static MockAccount dummyAccount(int accountID) {
        return dummyAccount(accountID, "savings");
    }

    public static MockAccount dummyAccount(int accountID, String name, long userBalance) {
        var account = new MockAccount();
        account.setId(accountID);
        account.setName(name);
        account.setUserBalance(userBalance);
        return account;
    }

    public static MockAccount dummyAccount(int accountID, String name) {
        return new MockAccount(accountID, 1, 10000, name);
    }

    public static List<MockAccount> dummyAccountsWithName(String name) {
        return List.of(dummyAccount(1, name), dummyAccount(2, name), dummyAccount(3, name), dummyAccount(4, name));
    }

    public static List<MockAccount> dummyAccountsWithNameAndUserBalance(String name, long userBalance) {
        return List.of(dummyAccount(1, name, userBalance), dummyAccount(2, name, userBalance), dummyAccount(3, name, userBalance), dummyAccount(4, name, userBalance));
    }

}
