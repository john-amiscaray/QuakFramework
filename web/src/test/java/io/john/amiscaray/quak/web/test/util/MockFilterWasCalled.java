package io.john.amiscaray.quak.web.test.util;

import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import jakarta.servlet.Filter;

import java.util.concurrent.CompletableFuture;

@ManagedType
public class MockFilterWasCalled extends CompletableFuture<Filter> {



}
