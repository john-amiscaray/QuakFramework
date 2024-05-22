package io.john.amiscaray.backend.framework.web.test.stub;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MockUserInfo {

    private String username;
    private int age;
    private String address;

    public static MockUserInfo dummyUser() {
        return new MockUserInfo("John", 21, "123 Some Street");
    }

    public static MockUserInfo dummyUser(int age) {
        return new MockUserInfo("John", age, "123 Some Street");
    }

    public static List<MockUserInfo> dummyUsers() {
        return List.of(dummyUser());
    }

}
