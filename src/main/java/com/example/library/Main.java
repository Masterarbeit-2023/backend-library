package com.example.library;

import com.example.library.generator.ProjectFileGenerator;
import com.example.library.generator.ProjectGenerator;
import com.example.library.generator.config.Configuration;
import com.example.library.generator.helper.ProjectDeclaration;
import com.example.library.generator.helper.Reader;

import java.util.List;

import static com.example.library.generator.YamlReader.readYaml;

public class Main {

    public static Configuration configuration;
    public static ProjectDeclaration project;

    public static void generate(String projectRoot, String classToIgnored) {
        configuration = readYaml("config.yaml");
        System.out.println(configuration);
        project = Reader.readProject(projectRoot, classToIgnored);
        List<ProjectDeclaration> generatedProjects = ProjectGenerator.generateProjectDeclaration(project, configuration);
        //List<ProjectDeclaration> generatedProjects = ProjectGenerator.generateProjectDeclarationWithProjectConfig(project, configuration);
        //ProjectFileGenerator.generateProject(generatedProjects, configuration);
        //ProjectFileGenerator.generateProjectServerless(generatedProjects, configuration);
        System.out.println(generatedProjects);
    }
}
