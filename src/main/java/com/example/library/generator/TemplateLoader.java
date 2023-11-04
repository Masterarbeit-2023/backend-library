package com.example.library.generator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class TemplateLoader {

    public static String loadTemplate(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}
