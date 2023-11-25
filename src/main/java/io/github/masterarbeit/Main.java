package io.github.masterarbeit;

import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.files.ProjectFileGenerator;
import io.github.masterarbeit.generator.helper.ProjectDeclaration;
import io.github.masterarbeit.generator.helper.Reader;
import io.github.masterarbeit.generator.project.ProjectGenerator;

import java.util.List;

import static io.github.masterarbeit.generator.YamlReader.readYaml;

public class Main {

    public static Configuration configuration;
    public static ProjectDeclaration project;

    public static void generate(String projectRoot, String classToIgnored) {
        configuration = readYaml("config.yaml");
        System.out.println(configuration);
        project = Reader.readProject(projectRoot, classToIgnored);
        List<ProjectDeclaration> generatedProjects = new ProjectGenerator().generate(project, configuration);
        System.out.println();
        ProjectFileGenerator.generateProjectFiles(generatedProjects, configuration);
        System.out.println();
    }
}
