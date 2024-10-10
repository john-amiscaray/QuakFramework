package io.john.amiscaray.quak.generator.util;

import com.github.javaparser.ast.expr.*;

import java.util.Optional;

public class ParserUtils {

    public static Optional<Expression> getAnnotationMemberValue(AnnotationExpr annotation, String key) {
        if (annotation instanceof SingleMemberAnnotationExpr singleMemberAnnotationExpr) {
            return Optional.of(singleMemberAnnotationExpr.getMemberValue());
        } else if (annotation instanceof NormalAnnotationExpr normalAnnotationExpr) {
            return normalAnnotationExpr.getPairs()
                    .stream()
                    .filter(keyValuePair -> key.equals(keyValuePair.getNameAsString()))
                    .map(MemberValuePair::getValue)
                    .findFirst();
        } else {
            // Value cannot be found
            return Optional.empty();
        }
    }

}
