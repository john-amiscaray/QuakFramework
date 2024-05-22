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

}
