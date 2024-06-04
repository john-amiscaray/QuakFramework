package io.john.amiscaray.backend.framework.core.di.stub.pojo;

import java.util.Date;

public record MockUserAccount(MockUser owner, Date createdOn, Long balance, String accountName) {

}
