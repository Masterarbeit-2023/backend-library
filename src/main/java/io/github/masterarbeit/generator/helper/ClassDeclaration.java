package io.github.masterarbeit.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDeclaration {

    String packageDeclaration;
    List<ImportDeclaration> imports;
    List<String> annotations;
    String name;
    List<String> extendedTypes;
    List<FieldDeclaration> fields;
    List<MethodDeclaration> methods = new ArrayList<>();
    ProjectDeclaration project;

    public boolean containsAnnotation(String annotationName) {
        for (MethodDeclaration method:methods) {
            if (method.containsAnnotation(annotationName)) {
                return true;
            }
        }
        return false;
    }
    public boolean containsApiFunctionAnnotation(){
        return containsAnnotation("ApiFunction");
    }


    public void addMethod(MethodDeclaration methodDeclaration) {
        methods.add(methodDeclaration);
    }
}
