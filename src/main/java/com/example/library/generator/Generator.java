package com.example.library.generator;

import com.example.library.generator.config.Configuration;
import com.example.library.generator.config.Infrastructure;
import com.example.library.generator.config.ProviderEnum;
import com.example.library.generator.helper.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {

    public static void generateTemplateAndSaveFile(String templatePath, Map<String, String> values, Path savePath) {
        String template;

        template = TemplateLoader.loadTemplate(templatePath);

        String processedTemplate = TemplateProcessor.processTemplate(template, values);

        Writer.writeStringToJavaFile(processedTemplate, savePath);
    }

    private static String generateFieldString(FieldDeclaration field) {
        StringBuilder builder = new StringBuilder();
        for (String annotation : field.getAnnotations()) {
            builder.append("@").append(annotation).append("\n");
        }
        builder.append(field.getType()).append(" ").append(field.getName()).append(";");
        return builder.toString();
    }


    private static String generateMethodStringWithProjectConfig(MethodDeclaration method) {
        Map<String, String> values = new HashMap<>();


        return "";
    }

    private static String generateMethodStringWithFunctionConfig(MethodDeclaration method) {
        Map<String, String> values = new HashMap<>();


        return "";
    }

    public static List<ProjectDeclaration> generateProjectDeclarationWithProjectConfig(ProjectDeclaration projectDeclaration, Configuration configuration) {
        Infrastructure infrastructure = configuration.getInfrastructure();
        ProviderEnum provider = configuration.getProvider();

        switch (infrastructure) {
            case TRADITIONAL -> {
                return generateProjectDeclarationTraditional(projectDeclaration);
            }
            case SERVERLESS -> {
                return generateProjectDeclarationServerless(projectDeclaration, provider);
            }
            case MICROSERVICES -> {
                return generateProjectDeclarationMicroservice(projectDeclaration, provider);
            }

        }

        return new ArrayList<>();
    }

    private static List<ProjectDeclaration> generateProjectDeclarationTraditional(ProjectDeclaration projectDeclaration) {
        List<ProjectDeclaration> projectDeclarations = new ArrayList<>();
        return projectDeclarations;
    }

    private static List<ProjectDeclaration> generateProjectDeclarationServerless(ProjectDeclaration projectDeclaration, ProviderEnum provider) {
        List<ProjectDeclaration> projectDeclarations = new ArrayList<>();
        for (ClassDeclaration classDeclaration : projectDeclaration.getClassDeclarations()) {
            ProjectDeclaration newProjectDeclaration;
            if (classDeclaration.containsApiFunctionAnnotation()) {
                for (MethodDeclaration method : classDeclaration.getMethods()) {
                    if (method.containsAnnotationApiFunction()) {
                        newProjectDeclaration = new ProjectDeclaration();
                        ClassDeclaration methodClassDeclaration = new ClassDeclaration();

                        methodClassDeclaration.setName(method.getName());
                        methodClassDeclaration.setPackageDeclaration("com.example");

                        MethodDeclaration methodDeclaration = new MethodDeclaration();
                        methodDeclaration.setName(method.getName());
                        methodDeclaration.setBody(method.getBody());
                        methodDeclaration.setReturnType(method.getReturnType());
                        methodDeclaration.setAnnotations(method.getAnnotations());
                        methodDeclaration.setParameters(method.getParameters());
                        methodClassDeclaration.addMethod(methodDeclaration);

                        newProjectDeclaration.addClassDeclaration(methodClassDeclaration);
                        projectDeclarations.add(newProjectDeclaration);
                    }
                }
            }
        }
        return projectDeclarations;
    }

    private static List<ProjectDeclaration> generateProjectDeclarationMicroservice(ProjectDeclaration projectDeclaration, ProviderEnum provider) {
        List<ProjectDeclaration> projectDeclarations = new ArrayList<>();
        return projectDeclarations;
    }

    public static void generateProjectDeclarationWithFunctionConfig(ProjectDeclaration projectDeclaration) {
        Infrastructure infrastructure = Testing.configuration.getInfrastructure();
        ProviderEnum provider = Testing.configuration.getProvider();


    }
}
