package com.example.library.generator;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.generator.config.Configuration;
import com.example.library.generator.config.Function;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        for (ClassDefinition classDefinition : classDefinitions) {
            List<Node> nodes = classDefinition.classOrInterfaceDeclaration.getChildNodes();
            Modifier modifier = (Modifier) nodes.get(0);
            String name = ((SimpleName) nodes.get(1)).getIdentifier();
            List<FieldDeclaration> fields = new ArrayList<>();
            List<MethodDeclaration> methods = new ArrayList<>();
            int counter = 2;
            while (counter < nodes.size() && nodes.get(counter) instanceof FieldDeclaration) {
                fields.add((FieldDeclaration) nodes.get(counter));
                counter++;
            }
            while (counter < nodes.size() && nodes.get(counter) instanceof MethodDeclaration) {
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
        String template;
        try {
            template = TemplateLoader.loadTemplate("src/main/java/com/example/library/generator/templates/traditional/ClassTemplate.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Map<String, String> valuesMap = new HashMap<>();
        StringBuilder fieldsString = new StringBuilder();
        for (FieldDeclaration fieldDeclaration : fields) {
            VariableDeclarator variable = fieldDeclaration.getVariables().get(0);
            fieldsString.append(variable.getTypeAsString()).append(" ").append(variable.getNameAsString()).append(";\n");
        }
        String methodTemplate;
        try {
            methodTemplate = TemplateLoader.loadTemplate("src/main/java/com/example/library/generator/templates/traditional/HttpTemplate.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        StringBuilder methodsString = new StringBuilder();
        for (MethodDeclaration methodDeclaration : methods) {
            methodDeclaration.getType();

            Map<String, String> methodValuesMap = new HashMap<>();
            NodeList<AnnotationExpr> annotationExprs = methodDeclaration.getAnnotations();
            List<AnnotationExpr> annotations = annotationExprs.stream().filter(annotationExpr ->
                    !Objects.equals(((Name) annotationExpr.getChildNodes().get(0)).asString(), "HttpTrigger") && !Objects.equals(((Name) annotationExpr.getChildNodes().get(0)).asString(), "ApiFunction")
            ).toList();
            String annotationString = "";
            for (AnnotationExpr annotationExpr: annotations) {
                annotationString += "\n@"+((Name) annotationExpr.getChildNodes().get(0)).asString();
            }

            List<AnnotationExpr> annotation = annotationExprs.stream().filter(annotationExpr ->
                    ((Name) annotationExpr.getChildNodes().get(0)).asString().equals("HttpTrigger")
            ).toList();
            String requestType;
            if (annotation.get(0).getChildNodes().size()==1) {
                requestType = "GET";
            } else {
                requestType = annotation.get(0).getChildNodes().get(1).getChildNodes().get(1).toString().replace("HttpMethod.","");
            }
            methodValuesMap.put("ANNOTATION", annotationString);
            methodValuesMap.put("REQUEST_TYPE", requestType);
            methodValuesMap.put("RETURN_TYPE", methodDeclaration.getTypeAsString());
            methodValuesMap.put("FUNCTION_NAME", methodDeclaration.getNameAsString());
            String parameterString = "";
            for (Parameter parameter : methodDeclaration.getParameters()) {
                parameterString += parameter.getTypeAsString() + " " + parameter.getNameAsString() + ", ";
            }
            methodValuesMap.put("PARAMETER", parameterString.substring(0, parameterString.length() - 2));
            methodValuesMap.put("BODY", methodDeclaration.getBody().get().toString());
            methodsString.append(TemplateProcessor.processTemplate(methodTemplate, methodValuesMap)).append("\n");
        }


        valuesMap.put("ANNOTATION", "");
        valuesMap.put("CLASS_NAME", name);
        valuesMap.put("FIELDS", fieldsString.toString());
        valuesMap.put("METHODS", methodsString.toString());

        String processedTemplate = TemplateProcessor.processTemplate(template, valuesMap);

        System.out.println(processedTemplate);

        // Optionally, write to a .java file
        try {
            Files.write(Paths.get("GeneratedClass.java"), processedTemplate.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        generateProject2("Test", "test", "com.example");
    }

    public static void generateProject(String name) {
        BashScriptExecutor.execute("src/main/java/com/example/library/generator/bash/generateProject.sh", "Test");
    }

    public static void generateProject2(String name, String artifactId, String groupId) {
        String path = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().toString();
        File rootFolder = new File(path + "/" + name);
        File testFolder = new File(rootFolder.getPath() + "/src/test/java/"+ groupId.replace(".", "//"));
        File srcFolder = new File(rootFolder.getPath() +"/src/main/java/" + groupId.replace(".", "//"));

        testFolder.mkdirs();
        srcFolder.mkdirs();
        String pomTemplate;
        try {
            pomTemplate = TemplateLoader.loadTemplate("src/main/java/com/example/library/generator/templates/pom.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("GROUP_ID", groupId);
        valuesMap.put("ARTIFACT_ID", artifactId);

        String processedTemplate = TemplateProcessor.processTemplate(pomTemplate, valuesMap);
        // Optionally, write to a .java file
        try {
            Files.write(Paths.get(rootFolder.getPath()+"\\pom.xml"), processedTemplate.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mainClassTemplate;
        try {
            pomTemplate = TemplateLoader.loadTemplate("src/main/java/com/example/library/generator/templates/traditional/MainClass.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        valuesMap = new HashMap<>();
        valuesMap.put("PACKAGE_NAME", groupId);

        processedTemplate = TemplateProcessor.processTemplate(pomTemplate, valuesMap);

        System.out.println(processedTemplate);

        // Optionally, write to a .java file
        try {
            Files.write(Paths.get(srcFolder.getPath()+"\\Main.java"), processedTemplate.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
