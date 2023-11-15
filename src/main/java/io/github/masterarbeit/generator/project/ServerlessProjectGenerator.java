package io.github.masterarbeit.generator.project;

import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.generator.helper.method.*;
import io.github.masterarbeit.util.HttpMethod;
import io.github.masterarbeit.util.StringUtil;
import org.apache.maven.model.Model;

import java.util.List;

public class ServerlessProjectGenerator extends ProjectGenerator {

    private static ServerlessProjectGenerator instance = null;

    private ServerlessProjectGenerator() {

    }

    public static ServerlessProjectGenerator getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new ServerlessProjectGenerator();
        return instance;
    }

    protected ProjectDeclaration processMethodDeclaration(MethodDeclaration method) {

        ProjectDeclaration newProject = new ProjectDeclaration();
        newProject.setName(method.getName());

        ClassDeclaration newClass = new ClassDeclaration();

        newClass.setName(StringUtil.capitalize(method.getName()));
        newClass.setPackageDeclaration("com.example" + method.getClazz().getPackageDeclaration().replace(Main.configuration.getBase_package(), ""));

        List<FieldDeclaration> fields = filterNeededFields(method.getClazz().getFields(), method.getBody());
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
        createNeededClasses(imports, newProject).forEach(newProject::addClassDeclaration);
        Model newPom = method.getClazz().getProject().getPom().clone();
        newPom.setName(method.getName());
        newPom.setArtifactId(method.getName());
        newPom.setDependencies(getNeededDependencies(newProject, newPom.getDependencies()));
        newProject.setPom(newPom);

        return newProject;
    }

    private MethodDeclaration initializeMethod(MethodDeclaration method) {
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
}
