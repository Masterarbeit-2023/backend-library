package com.example.library.generator;

import com.example.library.Main;
import com.example.library.generator.config.Configuration;
import com.example.library.generator.config.Infrastructure;
import com.example.library.generator.config.ProviderEnum;
import com.example.library.generator.helper.Annotation;
import com.example.library.generator.helper.ClassDeclaration;
import com.example.library.generator.helper.MethodDeclaration;
import com.example.library.generator.helper.ProjectDeclaration;
import com.example.library.util.StringUtil;
import org.apache.maven.model.Model;

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
        List<ProjectDeclaration> generatedProjects = new ArrayList<>();
        for (ClassDeclaration clazz : project.getClassDeclarations()) {
            for (MethodDeclaration method : clazz.getMethods()) {
                if (method.containsAnnotationApiFunction()){
                    process(generatedProjects, method, configuration);
                }
            }
        }
        return generatedProjects;
    }

    public static void process(List<ProjectDeclaration> projectDeclarations, MethodDeclaration method, Configuration configuration){
        switch (configuration.getInfrastructure()) {
            case SERVERLESS -> {
                System.out.println("SERVERLESS");
                ProjectDeclaration newProject = new ProjectDeclaration();
                newProject.setName(method.getName());

                ClassDeclaration newClass = new ClassDeclaration();

                newClass.setName(StringUtil.capitalize(method.getName()));
                newClass.setPackageDeclaration("com.example");
                newClass.setImports(method.getClazz().getImports());
                newClass.setFields(method.getClazz().getFields());

                MethodDeclaration newMethod = new MethodDeclaration();
                newMethod.setName(method.getName());
                newMethod.setBody(method.getBody());
                newMethod.setReturnType(method.getReturnType());
                newMethod.setAnnotations(processServerlessAnnotations(method.getName(), configuration.getProvider()));
                newMethod.setParameters(method.getParameters());

                newClass.addMethod(newMethod);
                newProject.addClassDeclaration(newClass);
                Model newPom = method.getClazz().getProject().getPom();
                newPom.setName(method.getName());
                newPom.setArtifactId(method.getName());
                newProject.setPom(newPom);

                projectDeclarations.add(newProject);
            }
            case MICROSERVICES -> {
                System.out.println("MICROSERVICES");
                ProjectDeclaration newProject = new ProjectDeclaration();


                projectDeclarations.add(newProject);
            }
            case TRADITIONAL -> {
                System.out.println("TRADITIONAL");
                ProjectDeclaration newProject;
                if (projectDeclarations.isEmpty()){
                    newProject = new ProjectDeclaration();
                } else {
                    newProject = projectDeclarations.get(0);
                }

                projectDeclarations.clear();
                projectDeclarations.add(newProject);
            }
        }
    }
}
