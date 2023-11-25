package io.github.masterarbeit.generator.project;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.config.Function;
import io.github.masterarbeit.generator.config.Infrastructure;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.generator.helper.method.*;
import io.github.masterarbeit.util.HttpMethod;
import io.github.masterarbeit.util.StringUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static io.github.masterarbeit.util.Constants.*;

public class ProjectGenerator {

    public List<ProjectDeclaration> generate(ProjectDeclaration project, Configuration configuration) {
        List<ProjectDeclaration> generatedProjects = new ProjectGenerator().generateProjectDeclaration(project);

        ProjectDeclaration azureProject = null;
        ProjectDeclaration awsProject = null;
        ProjectDeclaration googleProject = null;
        ProjectDeclaration onPremisesProject = null;

        List<ProjectDeclaration> newProjects = new ArrayList<>();

        for (ProjectDeclaration p : generatedProjects) {
            Function functionConfig = configuration.getConfigurationForFunction(p.getName());
            if (functionConfig.getInfrastructure() == Infrastructure.TRADITIONAL) {
                switch (functionConfig.getProvider()) {
                    case AZURE -> azureProject = combineProjectWithExistsCheck(azureProject, p);
                    case AWS -> awsProject = combineProjectWithExistsCheck(awsProject, p);
                    case GOOGLE -> googleProject = combineProjectWithExistsCheck(googleProject, p);
                    case ON_PREMISES -> onPremisesProject = combineProjectWithExistsCheck(onPremisesProject, p);
                }
            } else {
                newProjects.add(p);
            }
        }
        newProjects.addAll(Stream.of(azureProject, awsProject, googleProject, onPremisesProject).filter(Objects::nonNull).toList());

        return newProjects;
    }

    public ProjectDeclaration combineProjectWithExistsCheck(ProjectDeclaration existsCheckProject, ProjectDeclaration projectToAdd) {
        if (existsCheckProject != null) {
            return existsCheckProject.combine(projectToAdd);
        }
        return projectToAdd;
    }

    public List<ProjectDeclaration> generateProjectDeclaration(ProjectDeclaration project) {
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
        newProject.setName(method.getName());

        ClassDeclaration newClass = new ClassDeclaration();

        newClass.setName(StringUtil.capitalize(method.getName()));
        newClass.setPackageDeclaration(GENERATED_PROJECTS_BASE_PACKAGE + method.getClazz().getPackageDeclaration().replace(Main.configuration.getBase_package(), ""));

        List<FieldDeclaration> fields = filterNeededFields(method.getClazz().getFields(), method.getBody());
        newClass.setFields(fields);
        List<String> imports = method.getClazz().getImports().stream().filter(value -> !value.contains(LIBRARY_BASE_PACKAGE)).toList();
        imports = imports.stream().map(value -> value.replace(Main.configuration.getBase_package(), GENERATED_PROJECTS_BASE_PACKAGE)).toList();
        newClass.setImports(imports);

        MethodDeclaration newMethod = initializeMethod(method);

        newMethod.setName(method.getName());
        newMethod.setBody(method.getBody());
        newMethod.setReturnType(method.getReturnType());
        newMethod.setAnnotations(method.getAnnotations());
        newMethod.setParameters(method.getParameters());

        newClass.addMethod(newMethod);
        newProject.addClassDeclaration(newClass);
        createNeededClasses(imports, newProject).forEach(newProject::addOtherClass);
        Model newPom = method.getClazz().getProject().getPom().clone();
        newPom.setName(method.getName());
        newPom.setArtifactId(method.getName());
        newPom.setDependencies(getNeededDependencies(newProject, newPom.getDependencies()));
        newProject.setPom(newPom);
        newProject.setProperties(method.getClazz().getProject().getProperties());

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

    protected List<OtherClass> createNeededClasses(List<String> imports, ProjectDeclaration project) {
        List<String> classesToCreate = imports.stream().filter(value -> value.contains("com.example")).map(value -> value.replace("com.example.", "")).toList();
        List<OtherClass> createdClasses = new ArrayList<>();

        for (String s : classesToCreate) {
            String[] arr = s.split("\\.");
            OtherClass clazz = Main.project.getOtherClassByName(arr[arr.length - 1]);
            if (clazz != null) {
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
