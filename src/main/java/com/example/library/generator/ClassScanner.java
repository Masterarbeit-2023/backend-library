package com.example.library.generator;

import com.example.library.annotation.method.ApiFunction;
import com.example.library.annotation.parameter.DatabaseTrigger;
import com.example.library.annotation.parameter.HttpTrigger;
import com.example.library.annotation.parameter.RabbitMqTrigger;
import com.example.library.annotation.parameter.TimerTrigger;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static void componentScanning() {
        try {
            List<Class<?>> classesWithAnnotation = ClassScanner.getClassesWithAnnotation("src/main/java/com.example.library.test", ApiFunction.class);

            for (Class<?> clazz : classesWithAnnotation) {
                System.out.println("Class with MyAnnotation: " + clazz.getName());
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Class<?>> getClassesWithAnnotation(String packageName, Class<?> annotationClass)
            throws ClassNotFoundException, IOException {
        List<Class<?>> classesWithAnnotation = new ArrayList<>();

        String path = packageName.replace('.', '/');
        File directory = new File(path);
        if (!directory.exists()) {
            return classesWithAnnotation;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 5);
                    Class<?> clazz = Class.forName(className.replace("/", ".").replace("src.main.java.", ""));
                    CompilationUnit compilationUnit = StaticJavaParser.parse(file);
                    try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.contains(annotationClass.getName())) {
                                classesWithAnnotation.add(clazz);
                                Node node = compilationUnit.getChildNodes().get(3).getChildNodes().get(3).getChildNodes().get(4);
                                for (Method m : clazz.getMethods()) {
                                    if (m.getParameterAnnotations().length > 0 && m.getParameterAnnotations()[0].length > 0) {
                                        if (m.getParameterAnnotations()[0].length > 1) {
                                            throw new IllegalArgumentException("");
                                        }

                                        // Class annotationType = m.getParameterAnnotations()[0][0].annotationType();
                                        Annotation annotation = m.getParameterAnnotations()[0][0];
                                        Class annotationType = annotation.annotationType();
                                        if (annotationType == HttpTrigger.class || annotationType == TimerTrigger.class || annotationType == DatabaseTrigger.class || annotationType == RabbitMqTrigger.class) {
                                            if (annotationType == (HttpTrigger.class)) {
                                                ProjectGenerator.createHttpTriggerProject(annotation, clazz);
                                            } else if (annotationType == (TimerTrigger.class)) {
                                            } else if (annotationType == (DatabaseTrigger.class)) {
                                            } else if (annotationType == (RabbitMqTrigger.class)) {
                                            }
                                        }
                                    }

                                }
                                break; // Stop searching once the string is found
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    // Recursively scan sub-packages
                    String subPackageName = packageName + "." + file.getName();
                    classesWithAnnotation.addAll(getClassesWithAnnotation(subPackageName, annotationClass));
                }
            }
        }

        return classesWithAnnotation;
    }
}
