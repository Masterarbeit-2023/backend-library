package com.example.library.generator;

import com.example.library.generator.helper.FieldDeclaration;
import com.example.library.generator.helper.MethodDeclaration;
import com.example.library.generator.helper.Writer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Generator {

    public static String srcFolderPath = "";

    public static void generateTemplateAndSaveFile(String templatePath, Map<String, String> values, Path savePath) {
        String template;

        template = TemplateLoader.loadTemplate(templatePath);

        String processedTemplate = TemplateProcessor.processTemplate(template, values);

        Writer.writeStringToJavaFile(processedTemplate, savePath);
    }

    public static void generateProject(String name, String artifactId, String groupId) {
        String path = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().toString();
        File rootFolder = new File(path + "/Test/" + name);
        File testFolder = new File(rootFolder.getPath() + "/src/test/java/" + groupId.replace(".", "//"));
        File srcFolder = new File(rootFolder.getPath() + "/src/main/java/" + groupId.replace(".", "//"));
        srcFolderPath = srcFolder.getPath();
        testFolder.mkdirs();
        srcFolder.mkdirs();

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("GROUP_ID", groupId);
        valuesMap.put("ARTIFACT_ID", artifactId);

        generateTemplateAndSaveFile(
                "src/main/java/com/example/library/generator/templates/pom.txt",
                valuesMap,
                Paths.get(rootFolder.getPath() + "\\pom.xml")
        );

        valuesMap = new HashMap<>();
        valuesMap.put("PACKAGE_NAME", groupId);

        generateTemplateAndSaveFile(
                "src/main/java/com/example/library/generator/templates/traditional/MainClass.txt",
                valuesMap,
                Paths.get(srcFolder.getPath() + "\\Main.java")
        );
    }

    public static String generateFieldString(FieldDeclaration field) {
        StringBuilder builder = new StringBuilder();
        for(String annotation: field.getAnnotations()) {
            builder.append("@").append(annotation).append("\n");
        }
        builder.append(field.getType()).append(" ").append(field.getName()).append(";");
        return builder.toString();
    }


    public static String generateMethodString(MethodDeclaration method) {
        Map<String, String> values = new HashMap<>();


        return "";
    }

}
