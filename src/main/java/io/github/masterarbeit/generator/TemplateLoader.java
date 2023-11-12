package io.github.masterarbeit.generator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class TemplateLoader {

    public static String loadTemplate(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
