package com.example.library.generator;

import com.example.library.generator.config.Configuration;

import java.lang.annotation.Annotation;

import static com.example.library.generator.ClassScanner.componentScanning;
import static com.example.library.generator.YamlReader.readYaml;

public class ProjectGenerator {

    public static void main(String[] args) {
        Configuration configuration = readYaml("config.yaml");

        System.out.println(configuration.toString());

        componentScanning();
    }


    public static void createHttpTriggerProject(Annotation annotation, Class clazz) {
    }


}
