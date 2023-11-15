package io.github.masterarbeit.generator.project;

public class TraditionalProjectGenerator extends ProjectGenerator {

    private static TraditionalProjectGenerator instance = null;

    private TraditionalProjectGenerator() {

    }

    public static TraditionalProjectGenerator getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new TraditionalProjectGenerator();
        return instance;
    }
}
