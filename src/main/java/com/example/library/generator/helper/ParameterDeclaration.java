package com.example.library.generator.helper;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ParameterDeclaration {
    List<String> annotation;
    String type;
    String name;
}
