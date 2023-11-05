package com.example.library.generator;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    public static boolean WritePomXml(String path, Model model) {
        MavenXpp3Writer writer = new MavenXpp3Writer();
        try {
            writer.write(new FileWriter(path), model);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
