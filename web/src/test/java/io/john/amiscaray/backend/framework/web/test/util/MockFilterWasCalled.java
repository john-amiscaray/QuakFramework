package io.john.amiscaray.backend.framework.web.test.util;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.ManagedType;
import jakarta.servlet.Filter;

import java.util.concurrent.CompletableFuture;

@ManagedType
public class MockFilterWasCalled extends CompletableFuture<Filter> {



}
