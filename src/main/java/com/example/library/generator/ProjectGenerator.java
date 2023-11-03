package com.example.library.generator;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.generator.config.Configuration;
import com.example.library.generator.config.Function;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.List;

import static com.example.library.generator.ClassScanner.componentScanning;
import static com.example.library.generator.YamlReader.readYaml;

public class ProjectGenerator {

    public static void main(String[] args) {
        Configuration configuration = readYaml("config.yaml");
        System.out.println(configuration);

        // Methoden mit @ApiFunction herauslesen
        List<ClassDefinition> classDefinitions = componentScanning(ApiFunction.class);
        // Projekt für diese Funktion erstellen (außer bei traditional)
            // Eigenschaften herauslesen
                // Name, Parameter, Body, ...
        for (ClassDefinition classDefinition: classDefinitions) {
            List<Node> nodes = classDefinition.classOrInterfaceDeclaration.getChildNodes();
            Modifier modifier = (Modifier) nodes.get(0);
            String name = ((SimpleName) nodes.get(1)).getIdentifier();
            List<FieldDeclaration> fields = new ArrayList<>();
            List<MethodDeclaration> methods = new ArrayList<>();
            int counter = 2;
            while(counter < nodes.size() && nodes.get(counter) instanceof FieldDeclaration) {
                fields.add((FieldDeclaration) nodes.get(counter));
                counter++;
            }
            while(counter < nodes.size() && nodes.get(counter) instanceof MethodDeclaration) {
                methods.add((MethodDeclaration) nodes.get(counter));
                counter++;
            }
            // Generate
            Function function = configuration.getConfigurationForFunction(name);

            generate(modifier, name, fields, methods, function);
            System.out.println();
        }
        // Benötigte Hilfsfunktionen hinzufügen
    }


    public static void createHttpTriggerProject(ClassDefinition clazz) {
    }

    public static void generate(Modifier modifier, String name, List<FieldDeclaration> fields, List<MethodDeclaration> methods, Function function) {

    }

}
