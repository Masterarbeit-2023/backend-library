package com.example.library.generator.helper;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FieldDeclaration {

    String name;
    List<String> annotation;
    String type;
}
