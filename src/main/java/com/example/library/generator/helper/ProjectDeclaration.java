package com.example.library.generator.helper;

import lombok.Data;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectDeclaration {
    List<ClassDeclaration> classDeclarations = new ArrayList<>();
    Model pom;


    public void addClassDeclaration(ClassDeclaration classDeclaration) {
        classDeclarations.add(classDeclaration);
    }

}
