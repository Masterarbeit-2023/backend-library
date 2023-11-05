package com.example.library.generator.helper;

import java.lang.annotation.Annotation;

public class MethodDeclaration {
    Annotation annotation;
    String name;
    Class returnType;
    ParameterDeclaration[] parameters;
    BodyDeclaration body;
    ClassDefinition references;
}
