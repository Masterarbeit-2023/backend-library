package io.github.masterarbeit.generator.project;

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
}
