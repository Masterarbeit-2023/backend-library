package com.example.library.generator.helper;

import com.github.javaparser.ast.stmt.BlockStmt;
import lombok.Data;

import java.util.List;

@Data
public class MethodDeclaration {
    List<Annotation> annotations;
    String name;
    String returnType;
    List<ParameterDeclaration> parameters;
    BlockStmt body;
    ClassDeclaration references;

    public boolean containsAnnotation(String annotationName) {
        if (getAnnotations().stream().filter(annotation -> annotation.getName().equals(annotationName)).count() > 0) {
            return true;
        }
        return false;
    }

    public boolean containsAnnotationApiFunction() {
        return containsAnnotation("ApiFunction");
    }
}
