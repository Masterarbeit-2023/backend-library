package com.example.library.generator.helper;

import lombok.Data;

import java.util.List;

@Data
public class MethodDeclaration {
    List<String> annotation;
    String name;
    String returnType;
    List<ParameterDeclaration> parameters;
    BodyDeclaration body;
    ClassDeclaration references;
}
