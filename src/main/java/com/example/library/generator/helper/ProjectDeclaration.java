package com.example.library.generator.helper;

import lombok.Data;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectDeclaration {
    String name;
    List<ClassDeclaration> classDeclarations = new ArrayList<>();
    Model pom;


    public void addClassDeclaration(ClassDeclaration classDeclaration) {
        classDeclarations.add(classDeclaration);
    }

    public Pair<RequestType, Annotation> getRequestTypeAndAnnotationByMethodName(String functionName) {
        for (ClassDeclaration clazz : classDeclarations) {
            for (MethodDeclaration method : clazz.getMethods()) {
                if (functionName.equals(method.getName())) {
                    return method.getRequestTypeAndAnnotation();
                }
            }
        }
        return null;
    }
}
