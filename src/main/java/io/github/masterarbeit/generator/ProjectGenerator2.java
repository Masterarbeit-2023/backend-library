package io.github.masterarbeit.generator;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.generator.helper.method.*;
import io.github.masterarbeit.util.HttpMethod;
import io.github.masterarbeit.util.StringUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ProjectGenerator2 {


    public static List<ProjectDeclaration> generateProjectDeclaration(ProjectDeclaration project, Configuration configuration) {
        List<ProjectDeclaration> generatedProjects = new ArrayList<>();
        for (ClassDeclaration clazz : project.getClassDeclarations()) {
            for (MethodDeclaration method : clazz.getMethods()) {
                if (method.containsAnnotationApiFunction()) {
                    process(generatedProjects, method, configuration);
                } else {

                }
            }
        }
        return generatedProjects;
    }

    public static void process(List<ProjectDeclaration> projectDeclarations, MethodDeclaration method, Configuration configuration) {
        switch (configuration.getInfrastructure()) {
            case SERVERLESS -> {

                System.out.println("SERVERLESS");
                projectDeclarations.add(methodDeclarationToServerless(method));
            }
            case MICROSERVICES -> {
                System.out.println("MICROSERVICES");
                ProjectDeclaration newProject = methodDeclarationToMicroservice(method);

                projectDeclarations.add(newProject);
            }
            case TRADITIONAL -> {
                System.out.println("TRADITIONAL");
                ProjectDeclaration newProject;
                if (projectDeclarations.isEmpty()) {
                    newProject = new ProjectDeclaration();
                } else {
                    newProject = projectDeclarations.get(0);
                }

                projectDeclarations.clear();
                projectDeclarations.add(newProject);
            }
        }
    }


    private static ProjectDeclaration methodDeclarationToServerless(MethodDeclaration method) {

        ProjectDeclaration newProject = new ProjectDeclaration();
        newProject.setName(method.getName());

        ClassDeclaration newClass = new ClassDeclaration();

        newClass.setName(StringUtil.capitalize(method.getName()));
        newClass.setPackageDeclaration("com.example" + method.getClazz().getPackageDeclaration().replace(Main.configuration.getBase_package(), ""));

        List<FieldDeclaration> fields = filterFields(method.getClazz().getFields(), method.getBody());
        newClass.setFields(fields);
        List<String> imports = method.getClazz().getImports().stream().filter(value -> !value.contains("io.github.masterarbeit")).toList();
        imports = imports.stream().map(value -> value.replace(Main.configuration.getBase_package(), "com.example")).toList();
        newClass.setImports(imports);

        MethodDeclaration newMethod = initializeMethod(method);

        newMethod.setName(method.getName());
        newMethod.setBody(method.getBody());
        newMethod.setReturnType(method.getReturnType());
        newMethod.setAnnotations(method.getAnnotations());
        newMethod.setParameters(method.getParameters());

        newClass.addMethod(newMethod);
        newProject.addClassDeclaration(newClass);
        createOtherClasses(imports, newProject).forEach(newProject::addClassDeclaration);
        Model newPom = method.getClazz().getProject().getPom().clone();
        newPom.setName(method.getName());
        newPom.setArtifactId(method.getName());
        newPom.setDependencies(getNeededRepository(newProject, newPom.getDependencies()));
        newProject.setPom(newPom);

        return newProject;
    }

    private static List<Dependency> getNeededRepository(ProjectDeclaration project, List<Dependency> dependencies) {
        List<String> imports = new ArrayList<>();
        project.getClassDeclarations().forEach(value -> imports.addAll(value.getImports()));
        List<Dependency> newDependencies = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (isDependencyNeeded(imports, dependency)) {
                newDependencies.add(dependency);
            }
        }
        return newDependencies;
    }

    private static boolean isDependencyNeeded(List<String> imports, Dependency dependency) {
        String importString = dependency.getGroupId() + "." + dependency.getArtifactId().replace("-", ".").replace("spring.", "");

        for (String imp : imports) {
            if (imp.contains(dependency.getGroupId())) {
                return true;
            }
        }
        return false;
    }

    private static List<ClassDeclaration> createOtherClasses(List<String> imports, ProjectDeclaration project) {
        List<String> classesToCreate = imports.stream().filter(value -> value.contains("com.example")).map(value -> value.replace("com.example.", "")).toList();
        List<ClassDeclaration> createdClasses = new ArrayList<>();

        for (String s : classesToCreate) {
            String[] arr = s.split("\\.");
            ClassDeclaration clazz = Main.project.getClassDeclarationsByName(arr[arr.length - 1]);
            if (project.getClassDeclarationsByName(clazz.getName()) == null) {
                clazz.setPackageDeclaration(clazz.getPackageDeclaration().replace(Main.configuration.getBase_package(), "com.example"));
                clazz.setProject(null);
                clazz.setOtherClass(true);
                createdClasses.add(clazz);
            }
        }
        return createdClasses;
    }

    private static List<FieldDeclaration> filterFields(List<FieldDeclaration> fields, BlockStmt body) {
        return fields.stream().filter(field -> bodyContainsString(body, field.getName())).toList();
    }

    private static boolean bodyContainsString(BlockStmt body, String s) {
        for (Statement statement : body.getStatements()) {
            if (statement.toString().contains(s)) {
                return true;
            }
        }
        return false;
    }

    private static MethodDeclaration initializeMethod(MethodDeclaration method) {
        if (method.containsAnnotation("HttpTrigger")) {
            Annotation annotation = method.getAnnotation("HttpTrigger");
            HttpMethodDeclaration methodDeclaration = new HttpMethodDeclaration();
            try {
                methodDeclaration.setRequestType(HttpMethod.valueOf(annotation.getValues().get("httpMethod").replace("HttpMethod.", "")));
            } catch (Exception exception) {
                methodDeclaration.setRequestType(HttpMethod.GET);
            }

            methodDeclaration.setPathVariable(
                    method.getParameters().stream().filter(ParameterDeclaration::isPathVariable).map(ParameterDeclaration::getName).toList()
            );
            try {
                methodDeclaration.setAuthentication(annotation.getValues().get("authentication"));
            } catch (Exception e) {
                methodDeclaration.setAuthentication("");
            }
            return methodDeclaration;
        }
        if (method.containsAnnotation("TimerTrigger")) {
            Annotation annotation = method.getAnnotation("TimerTrigger");
            TimerMethodDeclaration methodDeclaration = new TimerMethodDeclaration();
            try {
                methodDeclaration.setCron(annotation.getValues().get("cron"));
            } catch (Exception e) {

            }
            try {
                methodDeclaration.setRate(Integer.parseInt(annotation.getValues().get("rate")));
            } catch (Exception e) {

            }

            return methodDeclaration;
        }
        if (method.containsAnnotation("DatabaseTrigger")) {
            Annotation annotation = method.getAnnotation("DatabaseTrigger");
            DatabaseMethodDeclaration methodDeclaration = new DatabaseMethodDeclaration();
            try {
                methodDeclaration.setQuery(annotation.getValues().get("query"));
            } catch (Exception e) {

            }
            return methodDeclaration;
        }
        if (method.containsAnnotation("RabbitMqTrigger")) {
            Annotation annotation = method.getAnnotation("RabbitMqTrigger");
            RabbitMqMethodDeclaration methodDeclaration = new RabbitMqMethodDeclaration();
            try {
                methodDeclaration.setMessage(annotation.getValues().get("message"));
            } catch (Exception e) {

            }
            try {
                methodDeclaration.setTopicName(annotation.getValues().get("topicName"));
            } catch (Exception e) {

            }
            return methodDeclaration;
        }
        return null;
    }

    private static ProjectDeclaration methodDeclarationToMicroservice(MethodDeclaration method) {
        return null;
    }
}
