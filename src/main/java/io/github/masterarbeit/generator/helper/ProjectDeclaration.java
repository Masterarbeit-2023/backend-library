package io.github.masterarbeit.generator.helper;

import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
