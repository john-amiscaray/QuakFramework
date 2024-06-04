package io.john.amiscaray.backend.framework.core.di.stub.pojo;

import java.util.Date;
import java.util.Objects;

public record MockUserAccount(MockUser owner, Date createdOn, Long balance) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockUserAccount that = (MockUserAccount) o;
        return Objects.equals(balance, that.balance) && Objects.equals(owner, that.owner) && Objects.equals(createdOn, that.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, createdOn, balance);
    }

}
