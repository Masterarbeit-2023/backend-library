package io.github.masterarbeit.generator.project;

import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.helper.ClassDeclaration;
import io.github.masterarbeit.generator.helper.FieldDeclaration;
import io.github.masterarbeit.generator.helper.ProjectDeclaration;
import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import io.github.masterarbeit.util.StringUtil;
import org.apache.maven.model.Model;

import java.util.List;

import static io.github.masterarbeit.util.Constants.GENERATED_PROJECTS_BASE_PACKAGE;
import static io.github.masterarbeit.util.Constants.LIBRARY_BASE_PACKAGE;

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
        createNeededClasses(imports, newProject).forEach(newProject::addClassDeclaration);
        Model newPom = method.getClazz().getProject().getPom().clone();
        newPom.setName(method.getName());
        newPom.setArtifactId(method.getName());
        newPom.setDependencies(getNeededDependencies(newProject, newPom.getDependencies()));
        newProject.setPom(newPom);

        return newProject;
    }


}
