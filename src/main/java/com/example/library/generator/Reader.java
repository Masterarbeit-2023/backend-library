package com.example.library.generator;

import com.example.library.generator.helper.ProjectDefinition;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    public static Model readPomXml(String path) {
        MavenXpp3Reader reader = new MavenXpp3Reader();

        MavenXpp3Writer writer = new MavenXpp3Writer();
        Model model = null;
        try {
            model = reader.read(new FileReader(path));
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException(e);
        }
        return model;
    }

    public static ProjectDefinition readProject(String path, String classToIgnored) {
        ProjectDefinition project = new ProjectDefinition();
        // Read Pom.xml
        Model model = readPomXml("pom.xml");
        project.setPom(model);

        // Read all existing classes
        List<Class<?>> classes = readAllClasses("src", classToIgnored);

        // Read all ClassDefinitions of Classes
        List<ClassDefinition> classDefinitions = classes.stream().map(Reader::readClassDefinitionOfClass).toList();
        project.setClassDefintionList(classDefinitions);

        return project;
    }

    public static List<Class<?>> readAllClasses(String packageName, String classToIgnored) {
        List<Class<?>> allClasses = new ArrayList<>();
        File[] files = getAllFilesInPackage(packageName);
        if (files == null) {
            return allClasses;
        }
        for (File file : files) {
            if (file.isFile()) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 5);
                Class<?> clazz = null;
                try {
                    className = className.replace("\\", ".").replace("src.main.java.", "");
                    clazz = Class.forName(className);
                    if(!className.equals(classToIgnored)) {
                        allClasses.add(clazz);
                    }

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                allClasses.addAll(readAllClasses(file.getPath(), classToIgnored));
            }
        }
        return allClasses;
    }

    public static File[] getAllFilesInPackage(String packageName) {
        String path = packageName.replace('.', '/');
        File directory = new File(path);
        if (!directory.exists()) {
            return new File[0];
        }

        return directory.listFiles();
    }

    public static ClassDefinition readClassDefinitionOfClass(Class clazz) {
        System.out.println("Class with MyAnnotation: " + clazz.getName());
        String resourcePath = ("src/main/java/" + clazz.getName()).replace('.', '/') + ".java";
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = StaticJavaParser.parse(new File(resourcePath));
            PackageDeclaration packageDeclaration = (PackageDeclaration) compilationUnit.getChildNodes().get(0);
            List<Node> importDeclaration = (List<Node>) compilationUnit.getChildNodes().subList(1, compilationUnit.getChildNodes().size() - 1);
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) compilationUnit.getChildNodes().get(compilationUnit.getChildNodes().size() - 1);
            return new ClassDefinition(packageDeclaration, importDeclaration, classOrInterfaceDeclaration);
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
