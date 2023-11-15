package io.github.masterarbeit.generator;

import io.github.masterarbeit.generator.config.*;
import io.github.masterarbeit.generator.helper.ClassDeclaration;
import io.github.masterarbeit.generator.helper.FieldDeclaration;
import io.github.masterarbeit.generator.helper.ProjectDeclaration;
import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectGeneratorTest {

    Model model = new Model();

    @BeforeEach
    void setup() {
        model = new Model();
        model.setArtifactId("test_test");
        model.setName("test_test");
        model.setModelVersion("4.0.0");
        model.setGroupId("org.example");
        model.setVersion("1.0-SNAPSHOT");
        model.setPackaging("jar");
        model.setPackaging("jar");
        Properties props = new Properties();
        props.put("maven.compiler.target", "17");
        props.put("maven.compiler.source", "17");
        props.put("project.build.sourceEncoding", "UTF-8");
        model.setProperties(props);
        List<Dependency> dependencies = new ArrayList<>();

        Dependency dependency1 = new Dependency();
        dependency1.setArtifactId("library");
        dependency1.setGroupId("io.github.masterarbeit");
        dependency1.setVersion("0.0.2");
        dependency1.setType("jar");
        dependencies.add(dependency1);

        Dependency dependency2 = new Dependency();
        dependency2.setArtifactId("spring-web");
        dependency2.setGroupId("org.springframework");
        dependency2.setVersion("6.0.12");
        dependency2.setType("jar");
        dependencies.add(dependency2);

        Dependency dependency3 = new Dependency();
        dependency3.setArtifactId("spring-data-jpa");
        dependency3.setGroupId("org.springframework.data");
        dependency3.setVersion("3.1.4");
        dependency3.setType("jar");
        dependencies.add(dependency3);
        model.setDependencies(dependencies);
    }


    @Test
    void generateProjectDeclarationTest() {
        Map<String, Function> map = new HashMap<>();
        Function function1 = new Function(ProviderEnum.AWS, "none");
        Function function2 = new Function(ProviderEnum.AZURE, "none");
        map.put("function_1", function1);
        map.put("function_2", function2);
        Configuration configuration = new Configuration(
                new Provider("azureKey"),
                new Provider("awsKey"),
                new Provider("googleKey"),
                new OnPremise("ip-address"),
                ProviderEnum.AZURE,
                ProviderEnum.AZURE,
                "yes",
                "org.example",
                ProviderEnum.AZURE,
                Infrastructure.TRADITIONAL,
                "none",
                map
        );
        ProjectDeclaration project = new ProjectDeclaration();
        project.setName("test_test");
        project.setPom(model);

        ClassDeclaration class1 = new ClassDeclaration();
        class1.setPackageDeclaration("org.example");
        List<String> imports = new ArrayList<>();
        imports.add("io.github.masterarbeit.annotation.method.ApiFunction");
        imports.add("io.github.masterarbeit.annotation.method.HttpTrigger");
        imports.add("io.github.masterarbeit.util.HttpMethod");
        imports.add("org.springframework.beans.factory.annotation.Autowired");
        imports.add("org.springframework.http.ResponseEntity");
        imports.add("org.springframework.stereotype.Service");
        class1.setImports(imports);
        List<String> annotations = new ArrayList<>();
        annotations.add("Service");
        class1.setAnnotations(annotations);

        class1.setName("Test2");

        List<FieldDeclaration> fields = new ArrayList<>();
        List<String> fieldAnnotations = new ArrayList<>();
        fieldAnnotations.add("Autowired");
        fields.add(new FieldDeclaration("testRepository", fieldAnnotations, "TestRepository"));
        class1.setFields(fields);

        List<MethodDeclaration> methods = new ArrayList<>();

        class1.setMethods(methods);

        class1.setProject(project);


        ClassDeclaration class2 = new ClassDeclaration();

        List<ClassDeclaration> classDeclarations = new ArrayList<>();
        classDeclarations.add(class1);
        classDeclarations.add(class2);

        project.setClassDeclarations(classDeclarations);


        assertEquals(new ArrayList<>(), ProjectGenerator.generateProjectDeclaration(project, configuration));
    }
}