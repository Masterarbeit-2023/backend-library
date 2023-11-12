package io.github.masterarbeit.generator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClassScanner {

    public static List<ClassDefinition> componentScanning(Class annotation) {
        try {
            List<Class<?>> classesWithAnnotation = getClassesWithAnnotation2("src/main/java/com.example.library.test", annotation);

            List<ClassDefinition> classDefinitions = new ArrayList<>();
            for (Class<?> clazz : classesWithAnnotation) {
                classDefinitions.add(getClassDefinitionOfClass(clazz));

            }
            return classDefinitions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static ClassDefinition getClassDefinitionOfClass(Class clazz) throws FileNotFoundException {
        System.out.println("Class with MyAnnotation: " + clazz.getName());
        String resourcePath = ("src/main/java/" + clazz.getName()).replace('.', '/') + ".java";
        CompilationUnit compilationUnit = StaticJavaParser.parse(new File(resourcePath));
        PackageDeclaration packageDeclaration = (PackageDeclaration) compilationUnit.getChildNodes().get(0);
        List<Node> importDeclaration = (List<Node>) compilationUnit.getChildNodes().subList(1, compilationUnit.getChildNodes().size() - 1);
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) compilationUnit.getChildNodes().get(compilationUnit.getChildNodes().size() - 1);

        return new ClassDefinition(packageDeclaration, importDeclaration, classOrInterfaceDeclaration);
    }

    public static File[] getAllFilesInPackage(String packageName) {
        String path = packageName.replace('.', '/');
        File directory = new File(path);
        if (!directory.exists()) {
            return new File[0];
        }

        return directory.listFiles();
    }

    public static boolean containsAnnotation(File file, Class annotationClass) {
        if (file.isFile() && file.getName().endsWith(".java")) {
            try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(annotationClass.getName())) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List<Class<?>> getClassesWithAnnotation2(String packageName, Class<?> annotationClass) throws ClassNotFoundException {
        List<Class<?>> classesWithAnnotation = new ArrayList<>();
        File[] files = getAllFilesInPackage(packageName);
        if (files == null) {
            return classesWithAnnotation;
        }
        for (File file : files) {
            if (containsAnnotation(file, annotationClass)) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 5);
                Class<?> clazz = Class.forName(className.replace("/", ".").replace("src.main.java.", ""));
                classesWithAnnotation.add(clazz);
            }
        }
        return classesWithAnnotation;
    }

    public static List<Class<?>> getAllClasses(String packageName) throws ClassNotFoundException {
        List<Class<?>> allClasses = new ArrayList<>();
        File[] files = getAllFilesInPackage(packageName);
        if (files == null) {
            return allClasses;
        }
        for (File file : files) {
            if(file.isFile()){
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 5);
                Class<?> clazz = Class.forName(className.replace("/", ".").replace("src.main.java.", ""));
                allClasses.add(clazz);
            } else {
                allClasses.addAll(getAllClasses(file.getPath()));
            }
        }
        return allClasses;
    }
}
