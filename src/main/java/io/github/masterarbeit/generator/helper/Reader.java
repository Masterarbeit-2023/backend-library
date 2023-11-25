package io.github.masterarbeit.generator.helper;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

    public static ProjectDeclaration readProject(String path, String classToIgnored) {
        ProjectDeclaration project = new ProjectDeclaration();
        // Read Pom.xml
        Model model = readPomXml("pom.xml");
        project.setPom(model);
        project.setName(model.getName());
        project.setProperties(Reader.readPropertyFiles());

        // Read all existing classes
        List<Class<?>> classes = readAllClasses("src", classToIgnored);

        // Read all ClassDefinitions of Classes
        List<ClassDeclaration> classDefinitions = classes.stream().filter(value -> {
            try {
                return Files.readString(Path.of("src/main/java/" + value.getName().replace('.', '/') + ".java")).contains("ApiFunction");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).map(clazz -> readClassDefinitionOfClass(clazz, project)).toList();

        List<OtherClass> otherClasses = classes.stream().filter(value -> {
            try {
                return !Files.readString(Path.of("src/main/java/" + value.getName().replace('.', '/') + ".java")).contains("ApiFunction");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).map(clazz -> {
            try {
                return new OtherClass(clazz.getPackageName(), clazz.getName().replace(clazz.getPackageName(), "").replace(".", ""), Files.readString(Path.of("src/main/java/" + clazz.getName().replace('.', '/') + ".java")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        project.setClassDeclarations(classDefinitions);
        project.setOtherClasses(otherClasses);

        return project;
    }

    public static List<Class<?>> readAllClasses(String packageName, String classToIgnored) {
        List<Class<?>> allClasses = new ArrayList<>();
        File[] files = Arrays.stream(getAllFilesInPackage(packageName)).filter(value -> !value.getPath().contains("src\\main\\resources")).toList().toArray(new File[0]);
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
                    if (!className.equals(classToIgnored)) {
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

    public static ClassDeclaration readClassDefinitionOfClass(Class clazz, ProjectDeclaration project) {
        String resourcePath = ("src/main/java/" + clazz.getName()).replace('.', '/') + ".java";
        CompilationUnit compilationUnit = null;
        try {
            ClassDeclaration classDeclaration = new ClassDeclaration();
            compilationUnit = StaticJavaParser.parse(new File(resourcePath));
            String packageDeclaration = ((PackageDeclaration) compilationUnit.getChildNodes().get(0)).getNameAsString();
            List<String> importDeclarations = new ArrayList<>();
            List<Node> importDeclaration = (List<Node>) compilationUnit.getChildNodes().subList(1, compilationUnit.getChildNodes().size() - 1);
            for (Node imp : importDeclaration) {
                importDeclarations.add(((com.github.javaparser.ast.ImportDeclaration) imp).getNameAsString());
            }
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) compilationUnit.getChildNodes().get(compilationUnit.getChildNodes().size() - 1);

            List<Node> nodes = classOrInterfaceDeclaration.getChildNodes();
            String name = classOrInterfaceDeclaration.getNameAsString();

            List<String> annotations = new ArrayList<>();
            List<String> extendedTypes = new ArrayList<>();
            List<FieldDeclaration> fields = new ArrayList<>();
            List<MethodDeclaration> methods = new ArrayList<>();
            extendedTypes = classOrInterfaceDeclaration.getExtendedTypes().stream().map(value -> value.asString()).toList();
            int counter = 0;
            while (counter < nodes.size() && nodes.get(counter) instanceof MarkerAnnotationExpr) {
                annotations.add(((MarkerAnnotationExpr) nodes.get(counter)).getNameAsString());
                counter++;
            }
            counter += 2;
            while (counter < nodes.size() && nodes.get(counter) instanceof com.github.javaparser.ast.body.FieldDeclaration field) {
                VariableDeclarator variable = field.getVariables().get(0);
                fields.add(
                        new FieldDeclaration(
                                variable.getNameAsString(),
                                field.getAnnotations().stream().map(NodeWithName::getNameAsString).toList(),
                                variable.getTypeAsString())
                );
                counter++;
            }
            while (counter < nodes.size() && nodes.get(counter) instanceof com.github.javaparser.ast.body.MethodDeclaration method) {
                MethodDeclaration methodDeclaration = new MethodDeclaration();
                List<Annotation> methodAnnotations = new ArrayList<>();
                for (Node node : method.getAnnotations()) {
                    Map<String, String> map = new HashMap<>();
                    if (node instanceof NormalAnnotationExpr normalAnnotationExpr) {
                        for (MemberValuePair pair : normalAnnotationExpr.getPairs()) {
                            map.put(pair.getNameAsString(), pair.getValue().toString());
                        }
                    }
                    methodAnnotations.add(new Annotation(node.getChildNodes().get(0).toString(), map));
                }

                methodDeclaration.setAnnotations(methodAnnotations);
                methodDeclaration.setName(method.getNameAsString());
                methodDeclaration.setReturnType(method.getType().asString());
                methodDeclaration.setParameters(
                        method.getParameters().stream().map(
                                value ->
                                        new ParameterDeclaration(
                                                value.getAnnotations().stream().map(NodeWithName::getNameAsString).toList(),
                                                value.getTypeAsString(),
                                                value.getNameAsString()
                                        )
                        ).toList()
                );
                methodDeclaration.setBody(method.getBody().get());
                methodDeclaration.setClazz(classDeclaration);
                methods.add(methodDeclaration);
                counter++;
            }
            classDeclaration.setPackageDeclaration(packageDeclaration);
            classDeclaration.setInterface(classOrInterfaceDeclaration.isInterface());
            classDeclaration.setImports(importDeclarations);
            classDeclaration.setAnnotations(annotations);
            classDeclaration.setName(name);
            classDeclaration.setExtendedTypes(extendedTypes);
            classDeclaration.setFields(fields);
            classDeclaration.setMethods(methods);
            classDeclaration.setProject(project);
            return classDeclaration;
            //return new ClassDeclaration(packageDeclaration, importDeclaration, classOrInterfaceDeclaration);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static List<Pair<String, String>> readPropertyFiles() {
        File[] files = getAllFilesInPackage("src\\main\\resources");
        List<Pair<String, String>> properties = new ArrayList<>();

        for (File file : files) {
            String name = file.getName();
            if (name.contains(".properties") || name.contains("application") && (name.contains(".yaml") || name.contains(".yml"))) {
                try {
                    properties.add(new Pair<>(name, Files.readString(Path.of(file.getPath()))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return properties;
    }
}
