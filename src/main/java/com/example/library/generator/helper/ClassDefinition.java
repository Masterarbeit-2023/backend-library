package com.example.library.generator.helper;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.List;

@Data
public class ClassDefinition {

    List<ImportDefinition> imports;
    List<Annotation> annotations;
    String name;


}
