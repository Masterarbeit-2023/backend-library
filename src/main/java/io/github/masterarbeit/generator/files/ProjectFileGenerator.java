package io.github.masterarbeit.generator.files;

import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.ProjectDeclaration;

import java.util.List;

public abstract class ProjectFileGenerator {

    public static void generateFiles(List<ProjectDeclaration> generatedProjects, Configuration configuration) {
        switch (configuration.getInfrastructure()) {
            case TRADITIONAL -> {
                new TraditionalProjectFileGenerator().generate(generatedProjects, configuration);
            }
            case MICROSERVICES -> {
                new MicroserviceProjectFileGenerator().generate(generatedProjects, configuration);
            }
            case SERVERLESS -> {
                new ServerlessProjectFilesGenerator().generate(generatedProjects, configuration);
            }
        }
    }

    protected void generate(List<ProjectDeclaration> generatedProjects, Configuration configuration) {

    }
}
