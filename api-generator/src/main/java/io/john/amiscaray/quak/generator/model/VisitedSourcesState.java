package io.john.amiscaray.quak.generator.model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record VisitedSourcesState(
        HashMap<ClassOrInterfaceDeclaration, ClassExpr> restModelClassToEntity,
        List<ClassOrInterfaceDeclaration> visitedRestModelClasses,
        List<ClassOrInterfaceDeclaration> visitedEntityClasses,
        List<ClassOrInterfaceDeclaration> visitedDIComponents) {

    public VisitedSourcesState() {
        this(new HashMap<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

}
