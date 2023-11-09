package com.example.library.generator;

import com.example.library.Main;
import com.example.library.generator.config.Configuration;
import com.example.library.generator.config.Infrastructure;
import com.example.library.generator.config.ProviderEnum;
import com.example.library.generator.helper.Annotation;
import com.example.library.generator.helper.ClassDeclaration;
import com.example.library.generator.helper.MethodDeclaration;
import com.example.library.generator.helper.ProjectDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectGenerator {

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
                        newProjectDeclaration.setName(method.getName());
                        ClassDeclaration methodClassDeclaration = new ClassDeclaration();

                        methodClassDeclaration.setName(method.getName());
                        methodClassDeclaration.setPackageDeclaration("com.example");
                        methodClassDeclaration.setFields(classDeclaration.getFields());

                        MethodDeclaration methodToGenerateDeclaration = new MethodDeclaration();
                        methodToGenerateDeclaration.setName(method.getName());
                        methodToGenerateDeclaration.setBody(method.getBody());
                        methodToGenerateDeclaration.setReturnType(method.getReturnType());
                        methodToGenerateDeclaration.setAnnotations(processServerlessAnnotations(method.getName(),provider));

                        methodToGenerateDeclaration.setParameters(method.getParameters());
                        methodClassDeclaration.addMethod(methodToGenerateDeclaration);

                        newProjectDeclaration.addClassDeclaration(methodClassDeclaration);
                        projectDeclarations.add(newProjectDeclaration);
                    }
                }
            }
        }
        return projectDeclarations;
    }

    private static List<Annotation> processServerlessAnnotations(String functionName, ProviderEnum provider) {
        List<Annotation> annotations = new ArrayList<>();
        switch (provider) {
            case AWS, GOOGLE -> {
                annotations.add(new Annotation("Override", null));
            }
            case AZURE -> {
                Map<String, String> map = new HashMap<>();
                map.put("name", functionName);
                annotations.add(new Annotation("FunctionName", map));
            }
        }
        return annotations;
    }

    private static List<ProjectDeclaration> generateProjectDeclarationMicroservice(ProjectDeclaration projectDeclaration, ProviderEnum provider) {
        List<ProjectDeclaration> projectDeclarations = new ArrayList<>();
        return projectDeclarations;
    }

    public static void generateProjectDeclarationWithFunctionConfig(ProjectDeclaration projectDeclaration) {
        Infrastructure infrastructure = Main.configuration.getInfrastructure();
        ProviderEnum provider = Main.configuration.getProvider();


    }

    public static List<ProjectDeclaration> generateProjectDeclaration(ProjectDeclaration project, Configuration configuration) {
        return null;
    }
}
