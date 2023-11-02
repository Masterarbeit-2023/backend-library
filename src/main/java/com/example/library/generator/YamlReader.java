package com.example.library.generator;

import com.example.library.generator.config.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;

public class YamlReader {
    public static Configuration readYaml(String pathToConfigFile) {
        try {
            // Create a YAML parser
            Yaml yaml = new Yaml();

            // Open the YAML file for reading
            FileReader reader = new FileReader(pathToConfigFile);

            // Parse the YAML file and load it into a Java object
            Configuration configuration = yaml.loadAs(reader, Configuration.class);

            // Close the file reader
            reader.close();

            // Now, 'data' contains the parsed YAML data as a Java object
            return configuration;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
