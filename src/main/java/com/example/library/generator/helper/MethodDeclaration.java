package com.example.library.generator.helper;

import com.github.javaparser.ast.stmt.BlockStmt;
import lombok.Data;

import java.util.List;

@Data
public class MethodDeclaration {
    List<Annotation> annotation;
    String name;
    String returnType;
    List<ParameterDeclaration> parameters;
    BlockStmt body;
    ClassDeclaration references;
}
