package io.john.amiscaray.backend.framework.test.core.di.stub.pojo;

public record MockEmployee (Long id, String name, String department) {

    public static MockEmployee mockEmployee() {
        return new MockEmployee(1L, "John", "Tech");
    }

}
