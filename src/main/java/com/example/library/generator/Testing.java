package com.example.library.generator;

import com.example.library.generator.config.Configuration;
import com.example.library.generator.helper.ProjectDeclaration;
import com.example.library.generator.helper.Reader;

import static com.example.library.generator.YamlReader.readYaml;

public class Testing {

    public static Configuration configuration;
    public static ProjectDeclaration project;

    public static void main(String[] args) {
        test("", new Exception().getStackTrace()[0].getClassName());
    }
    public static void test(String projectRoot, String classToIgnored) {
        configuration = readYaml("config.yaml");
        System.out.println(configuration);
        project = Reader.readProject(projectRoot, classToIgnored);
        System.out.println(project);
    }
}
