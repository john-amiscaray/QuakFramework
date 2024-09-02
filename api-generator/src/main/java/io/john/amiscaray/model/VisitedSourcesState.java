package io.john.amiscaray.model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record VisitedSourcesState(
        HashMap<ClassOrInterfaceDeclaration, ClassExpr> restModelClassToEntity,
        List<ClassOrInterfaceDeclaration> visitedRestModelClasses,
        List<ClassOrInterfaceDeclaration> visitedEntityClasses) {

    public VisitedSourcesState() {
        this(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
    }

}
