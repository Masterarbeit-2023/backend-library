package io.github.masterarbeit.generator.helper;

import io.github.masterarbeit.generator.TemplateProcessor;
import io.github.masterarbeit.generator.config.ProviderEnum;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Writer {

    public static Pair<String, String> generateProjectFolders(String name, String groupId) {
        String path = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().toString();
        File rootFolder = new File(path + "/Test/" + name);
        File testFolder = new File(rootFolder.getPath() + "/src/test/java/" + groupId.replace(".", "//"));
        File srcFolder = new File(rootFolder.getPath() + "/src/main/java/" + groupId.replace(".", "//"));
        testFolder.mkdirs();
        srcFolder.mkdirs();

        return new Pair(rootFolder.getPath(), srcFolder.getPath());
    }

    public static Pair<String, String> generateProjectFolders(String name) {
        return generateProjectFolders(name, "com.example");
    }

    public static boolean writePomXml(String path, Model model) {
        MavenXpp3Writer writer = new MavenXpp3Writer();
        try {
            writer.write(new FileWriter(path), model);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean writeStringToJavaFile(String content, Path savePath) {
        try {
            Files.write(savePath, content.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void generateServerlessTemplateAndSaveFile(ProviderEnum provider, RequestType requestType, Map<String, String> values, Path savePath) {
        Resource resource = new ClassPathResource("templates/serverless/" + provider.name().toLowerCase() + "/" + requestType + "Template.txt");

        String template = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            template = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String processedTemplate = TemplateProcessor.processTemplate(template, values);

        Writer.writeStringToJavaFile(processedTemplate, savePath);
    }


    public static void generateOtherClassAndSaveFile(Map<String, String> values, Path savePath) {
        Resource resource = new ClassPathResource("templates/ClassTemplate.txt");

        String template = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            template = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String processedTemplate = TemplateProcessor.processTemplate(template, values);

        Writer.writeStringToJavaFile(processedTemplate, savePath);
    }

    public static void generateDockerfile(Path path) {
        Resource resource = new ClassPathResource("templates/Dockerfile.txt");

        String template = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            template = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String processedTemplate = TemplateProcessor.processTemplate(template, new HashMap<>());
        Writer.writeStringToJavaFile(processedTemplate, path);
    }

    public void generateDockerCompose(Path path) {

        Resource resource = new ClassPathResource("templates/docker-compose.txt");

        String template = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            template = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String processedTemplate = TemplateProcessor.processTemplate(template, new HashMap<>());
        Writer.writeStringToJavaFile(processedTemplate, path);
    }


}
