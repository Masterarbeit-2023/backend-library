package io.github.masterarbeit.generator.files;

import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.util.Constants;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public abstract class ProjectFileGenerator {

    public static void generateProjectFiles(List<ProjectDeclaration> generatedProjects, Configuration configuration) {
        switch (configuration.getInfrastructure()) {
            case TRADITIONAL -> new TraditionalProjectFileGenerator().generate(generatedProjects, configuration);
            case MICROSERVICES -> new MicroserviceProjectFileGenerator().generate(generatedProjects, configuration);
            case SERVERLESS -> new ServerlessProjectFilesGenerator().generate(generatedProjects, configuration);
        }
    }

    protected void generate(List<ProjectDeclaration> projects, Configuration configuration) {

    }


    protected void generateOtherClasses(List<OtherClass> otherClasses, String basePackageString) {
        for (OtherClass clazz : otherClasses) {
            generateOtherClass(clazz, basePackageString);
        }
    }

    protected void generateOtherClass(OtherClass clazz, String basePackageString) {
        String basePackage = Main.configuration.getBase_package();
        String directoryPath = basePackageString + clazz.getPackageName().replace(basePackage + ".", File.separator);
        String filePath = directoryPath + File.separator + clazz.getClassName() + ".java";

        if (!new File(directoryPath).exists()) {
            new File(directoryPath).mkdirs();
        }

        String content = clazz.getContent().replace(basePackage.replace("\\", "."), Constants.GENERATED_PROJECTS_BASE_PACKAGE);

        Writer.writeStringToFile(content, Path.of(filePath));

    }

    protected String importsToString(List<String> imports) {
        StringBuilder tmpString = new StringBuilder();

        for (String s : imports) {
            tmpString.append("import ").append(s).append(";\n");
        }
        return tmpString.toString();
    }

    protected String fieldsToString(List<FieldDeclaration> fields) {

        StringBuilder tmpString = new StringBuilder();

        for (FieldDeclaration field : fields) {
            tmpString.append(annotationsToString(field.getAnnotations()));
            tmpString.append(field.getType()).append(" ").append(field.getName()).append(";\n");
        }
        return tmpString.toString();
    }

    protected String annotationsToString(List<String> annotations) {
        StringBuilder tmpString = new StringBuilder();

        for (String s : annotations) {
            tmpString.append("@").append(s).append("\n");
        }
        return tmpString.toString();
    }

    public void createPropertiesFiles(List<Pair<String, String>> properties, String propertiesPath) {
        new File(propertiesPath).mkdirs();
        for (Pair<String, String> property : properties) {
            Writer.writeStringToFile(property.getSecond(), Path.of(propertiesPath + File.separator + property.getFirst()));
        }
    }
}
