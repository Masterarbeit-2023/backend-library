package io.github.masterarbeit.generator.files;

import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.ProjectDeclaration;

import java.util.List;

public class MicroserviceProjectFileGenerator extends ProjectFileGenerator {

    public void generate(List<ProjectDeclaration> projects, Configuration configuration) {
        new TraditionalProjectFileGenerator().generate(projects, configuration);

    }
}
