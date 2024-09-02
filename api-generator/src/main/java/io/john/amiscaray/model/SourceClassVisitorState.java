package io.john.amiscaray.model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record SourceClassVisitorState(
        HashMap<ClassOrInterfaceDeclaration, ClassExpr> restModelClassToEntity,
        List<ClassOrInterfaceDeclaration> visitedEntityClasses) {

    public SourceClassVisitorState() {
        this(new HashMap<>(), new ArrayList<>());
    }

}
