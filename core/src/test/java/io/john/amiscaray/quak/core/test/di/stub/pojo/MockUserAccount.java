package io.john.amiscaray.quak.core.test.di.stub.pojo;

import java.util.Date;

public record MockUserAccount(MockUser owner, Date createdOn, Long balance, String accountName) {

}
