package io.github.masterarbeit.generator.project;

import io.github.masterarbeit.Main;

public abstract class ProjectGenerator {

    private static ProjectGenerator instance = null;

    public static ProjectGenerator getInstance() {
        if (instance != null) {
            return instance;
        }
        switch (Main.configuration.getInfrastructure()) {
            case SERVERLESS -> {
                instance = ServerlessProjectGenerator.getInstance();
            }
            case TRADITIONAL -> {
                instance = TraditionalProjectGenerator.getInstance();
            }
            case MICROSERVICES -> {
                instance = MicroserviceProjectGenerator.getInstance();
            }
        }
        return instance;
    }
}
