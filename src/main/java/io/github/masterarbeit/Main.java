package io.github.masterarbeit;

import io.github.masterarbeit.generator.ProjectGenerator2;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.ProjectDeclaration;
import io.github.masterarbeit.generator.helper.Reader;

import java.util.List;

import static io.github.masterarbeit.generator.YamlReader.readYaml;

public class Main {

    public static Configuration configuration;
    public static ProjectDeclaration project;

    public static void generate(String projectRoot, String classToIgnored) {
        configuration = readYaml("config.yaml");
        System.out.println(configuration);
        project = Reader.readProject(projectRoot, classToIgnored);
        List<ProjectDeclaration> generatedProjects = ProjectGenerator2.generateProjectDeclaration(project, configuration);
        //List<ProjectDeclaration> generatedProjects = ProjectGenerator.generateProjectDeclarationWithProjectConfig(project, configuration);
        //ProjectFileGenerator.generateProject(generatedProjects, configuration);
        //ProjectFileGenerator.generateProjectServerless(generatedProjects, configuration);
        System.out.println(generatedProjects);
    }
}
