package io.github.masterarbeit.generator.project;

public class MicroserviceProjectGenerator extends ProjectGenerator {

    private static MicroserviceProjectGenerator instance = null;

    private MicroserviceProjectGenerator() {

    }

    public static MicroserviceProjectGenerator getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new MicroserviceProjectGenerator();
        return instance;
    }
}
