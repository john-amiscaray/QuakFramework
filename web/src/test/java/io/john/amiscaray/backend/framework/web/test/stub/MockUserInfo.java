package io.john.amiscaray.backend.framework.web.test.stub;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
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

    public static MockUserInfo dummyUser(String name) {
        return new MockUserInfo(name, 21, "123 Some Street");
    }

    public static MockUserInfo dummyUser(int age, String name) {
        return new MockUserInfo(name, age, "123 Some Street");
    }

    public static List<MockUserInfo> dummyUsers() {
        return List.of(dummyUser());
    }

    public static List<MockUserInfo> dummyUsersWithName(String name) {
        return List.of(dummyUser(20, name), dummyUser(21, name), dummyUser(22, name));
    }

    public static List<MockUserInfo> dummyUsersWithAge(int age) {
        return List.of(dummyUser(age, "John"), dummyUser(age, "Elli"), dummyUser(age, "Arshia"));
    }

    public static List<MockUserInfo> dummyUsersWithAgeAndName(int age, String name) {
        return List.of(dummyUser(age, name), dummyUser(age, name));
    }

}
