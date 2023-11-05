package com.example.library.generator.helper;

import lombok.Data;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectDeclaration {
    List<ClassDeclaration> classDefintionList = new ArrayList<>();
    Model pom;


}
