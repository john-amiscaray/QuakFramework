package io.john.amiscaray.backend.framework.web.test.stub;

import lombok.*;

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
        return new MockAccount(1, 1, 10000, "savings");
    }

    public static MockAccount dummyAccount(int accountID) {
        return new MockAccount(accountID, 1, 10000, "savings");
    }

}
