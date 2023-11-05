package com.example.library.generator.helper;

import lombok.Data;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectDefinition {
    List<com.example.library.generator.ClassDefinition> classDefintionList = new ArrayList<>();
    Model pom;


}
