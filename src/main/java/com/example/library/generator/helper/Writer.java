package com.example.library.generator.helper;

import org.antlr.v4.runtime.misc.Pair;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Writer {

    public static String srcFolderPath = "";

    public static Pair<String, String> generateProjectFolders(String name, String groupId) {
        String path = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().toString();
        File rootFolder = new File(path + "/Test/" + name);
        File testFolder = new File(rootFolder.getPath() + "/src/test/java/" + groupId.replace(".", "//"));
        File srcFolder = new File(rootFolder.getPath() + "/src/main/java/" + groupId.replace(".", "//"));
        srcFolderPath = srcFolder.getPath();
        testFolder.mkdirs();
        srcFolder.mkdirs();

        return new Pair(rootFolder.getPath(), srcFolder.getPath());
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
}
