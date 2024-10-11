package io.john.amiscaray.quak.generator.test.assertions;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestSourceUtil {

    public static ClassOrInterfaceDeclaration parsedClassOrInterfaceDeclarationOf(String sourceCode) {
        var parser = new JavaParser();
        var parsedSource = parser.parse(sourceCode);
        var result = new CompletableFuture<ClassOrInterfaceDeclaration>();
        parsedSource.ifSuccessful(compilationUnit -> compilationUnit.accept(new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, CompletableFuture<ClassOrInterfaceDeclaration> future) {
                future.complete(n);
            }
        }, result));

        try {
            if (!parsedSource.isSuccessful()) {
                throw new IllegalArgumentException("Could not parse source code:\n" + parsedSource.getProblems());
            }
            return result.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
