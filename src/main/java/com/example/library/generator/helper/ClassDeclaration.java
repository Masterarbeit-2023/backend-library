package com.example.library.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ClassDeclaration {

    String packageDeclaration;
    List<ImportDeclaration> imports;
    List<String> annotations;
    String name;
    List<String> extendedTypes;
    List<FieldDeclaration> fields;
    List<MethodDeclaration> methods;
}
