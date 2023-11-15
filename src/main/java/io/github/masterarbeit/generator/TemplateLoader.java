package io.github.masterarbeit.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {

    public static String loadTemplate(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
