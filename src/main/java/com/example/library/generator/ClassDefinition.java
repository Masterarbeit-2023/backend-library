package com.example.library.generator;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.util.List;

public class ClassDefinition {
    PackageDeclaration packageDeclaration;
    List<Node> importDeclaration;
    ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    public ClassDefinition(PackageDeclaration packageDecl, List<Node> importDeclaration, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        this.packageDeclaration = packageDecl;
        this.importDeclaration = importDeclaration;
        this.classOrInterfaceDeclaration = classOrInterfaceDeclaration;
    }

    @Override
    public String toString() {
        return "ClassDefinition{" +
                "packageDeclaration=" + packageDeclaration +
                ", importDeclaration=" + importDeclaration +
                ", classOrInterfaceDeclaration=" + classOrInterfaceDeclaration +
                '}';
    }
}
