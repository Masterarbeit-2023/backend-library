package com.example.library.generator;

import com.example.library.generator.config.*;
import com.example.library.generator.helper.ClassDeclaration;
import com.example.library.generator.helper.ProjectDeclaration;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProjectGeneratorTest {

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
                ProviderEnum.AZURE,
                Infrastructure.TRADITIONAL,
                "none",
                map
        );

        List<ClassDeclaration> classDeclarations = new ArrayList<>();
        classDeclarations.add(new ClassDeclaration(
        ));

        ProjectDeclaration project = new ProjectDeclaration(
                "Test",
                classDeclarations,
                new Model()
        );

        assertEquals( new ArrayList<>(), ProjectGenerator.generateProjectDeclaration(project, configuration));
    }
}