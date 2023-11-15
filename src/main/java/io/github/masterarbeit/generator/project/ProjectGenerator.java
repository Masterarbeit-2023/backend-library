package io.github.masterarbeit.generator.project;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.generator.helper.method.*;
import io.github.masterarbeit.util.HttpMethod;
import org.apache.maven.model.Dependency;

import java.util.ArrayList;
import java.util.List;

import static io.github.masterarbeit.util.Constants.*;

public abstract class ProjectGenerator {

    private static ProjectGenerator instance = null;

    public static ProjectGenerator getInstance() {
        if (instance != null) {
            return instance;
        }
        switch (Main.configuration.getInfrastructure()) {
            case SERVERLESS -> {
                instance = ServerlessProjectGenerator.getInstance();
            }
            case TRADITIONAL -> {
                instance = TraditionalProjectGenerator.getInstance();
            }
            case MICROSERVICES -> {
                instance = MicroserviceProjectGenerator.getInstance();
            }
        }
        return instance;
    }

    public List<ProjectDeclaration> generateProjectDeclaration(ProjectDeclaration project, Configuration configuration) {
        List<ProjectDeclaration> generatedProjects = new ArrayList<>();
        for (ClassDeclaration clazz : project.getClassDeclarations()) {
            for (MethodDeclaration method : clazz.getMethods()) {
                if (method.containsAnnotationApiFunction()) {
                    generatedProjects.add(processMethodDeclaration(method));
                }
            }
        }
        return generatedProjects;
    }

    protected ProjectDeclaration processMethodDeclaration(MethodDeclaration method) {

        ProjectDeclaration newProject = new ProjectDeclaration();
        return newProject;
    }

    protected List<Dependency> getNeededDependencies(ProjectDeclaration project, List<Dependency> dependencies) {
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

    protected boolean isDependencyNeeded(List<String> imports, Dependency dependency) {
        for (String imp : imports) {
            if (imp.contains(dependency.getGroupId())) {
                return true;
            }
        }
        return false;
    }

    protected List<ClassDeclaration> createNeededClasses(List<String> imports, ProjectDeclaration project) {
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

    protected List<FieldDeclaration> filterNeededFields(List<FieldDeclaration> fields, BlockStmt body) {
        return fields.stream().filter(field -> bodyContainsString(body, field.getName())).toList();
    }

    protected boolean bodyContainsString(BlockStmt body, String s) {
        for (Statement statement : body.getStatements()) {
            if (statement.toString().contains(s)) {
                return true;
            }
        }
        return false;
    }

    protected MethodDeclaration initializeMethod(MethodDeclaration method) {
        if (method.containsAnnotation(HTTP_TRIGGER)) {
            Annotation annotation = method.getAnnotation(HTTP_TRIGGER);
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
        if (method.containsAnnotation(TIMER_TRIGGER)) {
            Annotation annotation = method.getAnnotation(TIMER_TRIGGER);
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
        if (method.containsAnnotation(DATABASE_TRIGGER)) {
            Annotation annotation = method.getAnnotation(DATABASE_TRIGGER);
            DatabaseMethodDeclaration methodDeclaration = new DatabaseMethodDeclaration();
            try {
                methodDeclaration.setQuery(annotation.getValues().get("query"));
            } catch (Exception e) {

            }
            return methodDeclaration;
        }
        if (method.containsAnnotation(RABBIT_MQ_TRIGGER)) {
            Annotation annotation = method.getAnnotation(RABBIT_MQ_TRIGGER);
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

}
