package io.github.masterarbeit.generator.helper;

import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.github.masterarbeit.util.ListUtil.combineWithoutDuplicates;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDeclaration {

    String packageDeclaration;
    boolean isInterface;
    boolean isOtherClass = false;
    List<String> imports;
    List<String> annotations;
    String name;
    List<String> extendedTypes;
    List<FieldDeclaration> fields;
    List<MethodDeclaration> methods = new ArrayList<>();
    ProjectDeclaration project;

    public boolean containsAnnotation(String annotationName) {
        for (MethodDeclaration method : methods) {
            if (method.containsAnnotation(annotationName)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsApiFunctionAnnotation() {
        return containsAnnotation("ApiFunction");
    }


    public void addMethod(MethodDeclaration methodDeclaration) {
        methods.add(methodDeclaration);
    }

    public ClassDeclaration combine(ClassDeclaration tmpClass) {
        ClassDeclaration clazz = new ClassDeclaration();

        clazz.setName(name);
        clazz.setPackageDeclaration(packageDeclaration);
        clazz.setImports(combineWithoutDuplicates(imports, tmpClass.imports));
        clazz.setAnnotations(combineWithoutDuplicates(annotations, tmpClass.annotations));
        clazz.setExtendedTypes(combineWithoutDuplicates(extendedTypes, tmpClass.extendedTypes));
        clazz.setFields(combineWithoutDuplicates(fields, tmpClass.getFields()));
        clazz.setMethods(combineWithoutDuplicates(methods, tmpClass.getMethods()));
        clazz.setProject(project);

        return clazz;
    }


}
