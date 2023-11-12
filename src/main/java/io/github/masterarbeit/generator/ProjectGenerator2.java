package io.github.masterarbeit.generator;

import io.github.masterarbeit.annotation.method.ApiFunction;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.config.Function;
import io.github.masterarbeit.generator.config.Infrastructure;
import io.github.masterarbeit.generator.helper.Writer;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static io.github.masterarbeit.generator.YamlReader.readYaml;

public class ProjectGenerator2 {

    public static String srcFolderPath = "";

    public static void main(String[] args) {
        Configuration configuration = readYaml("config.yaml");
        System.out.println(configuration);

        // Methoden mit @ApiFunction herauslesen
        List<ClassDefinition> classDefinitions = ClassScanner.componentScanning(ApiFunction.class);
        // Projekt für diese Funktion erstellen (außer bei traditional)
        // Eigenschaften herauslesen
        // Name, Parameter, Body, ...
        if (configuration.getInfrastructure().equals(Infrastructure.TRADITIONAL)) {
            generateProject("project", "project", "com.example");
        }
        for (ClassDefinition classDefinition : classDefinitions) {
            List<Node> nodes = classDefinition.classOrInterfaceDeclaration.getChildNodes();
            Modifier modifier = (Modifier) nodes.get(0);
            String name = ((SimpleName) nodes.get(1)).getIdentifier();
            List<FieldDeclaration> fields = new ArrayList<>();
            List<MethodDeclaration> methods = new ArrayList<>();
            int counter = 2;
            while (counter < nodes.size() && nodes.get(counter) instanceof FieldDeclaration) {
                fields.add((FieldDeclaration) nodes.get(counter));
                counter++;
            }
            while (counter < nodes.size() && nodes.get(counter) instanceof MethodDeclaration) {
                methods.add((MethodDeclaration) nodes.get(counter));
                counter++;
            }
            // Generate
            Function function = configuration.getConfigurationForFunction(name);

            generate(modifier, name, fields, methods, function, configuration);
            System.out.println();
        }
        // Benötigte Hilfsfunktionen hinzufügen
    }


    public static void generate(Modifier modifier, String name, List<FieldDeclaration> fields, List<MethodDeclaration> methods, Function function, Configuration configuration) {

        StringBuilder importBuilder = new StringBuilder();

        StringBuilder fieldsString = new StringBuilder();
        for (FieldDeclaration fieldDeclaration : fields) {
            VariableDeclarator variable = fieldDeclaration.getVariables().get(0);
            for (Node annotationExpr: fieldDeclaration.getAnnotations()) {
                MarkerAnnotationExpr annotation = (MarkerAnnotationExpr) annotationExpr;
                fieldsString.append("@" + annotation.getNameAsString() + "\n");
            }
            fieldsString.append(variable.getTypeAsString()).append(" ").append(variable.getNameAsString()).append(";\n");
        }
        String methodTemplate = TemplateLoader.loadTemplate("src/main/java/com/example/library/generator/templates/traditional/HttpTemplate.txt");

        StringBuilder methodsString = new StringBuilder();
        for (MethodDeclaration methodDeclaration : methods) {

            if (configuration.getInfrastructure().equals(Infrastructure.SERVERLESS) || configuration.getInfrastructure().equals(Infrastructure.MICROSERVICES)) {
                generateProject(methodDeclaration.getNameAsString(), methodDeclaration.getNameAsString().toLowerCase(), "com.example");
            }
            methodDeclaration.getType();

            Map<String, String> methodValuesMap = new HashMap<>();
            NodeList<AnnotationExpr> annotationExprs = methodDeclaration.getAnnotations();
            List<AnnotationExpr> annotations = annotationExprs.stream().filter(annotationExpr ->
                    !Objects.equals(((Name) annotationExpr.getChildNodes().get(0)).asString(), "HttpTrigger") && !Objects.equals(((Name) annotationExpr.getChildNodes().get(0)).asString(), "ApiFunction")
            ).toList();
            String annotationString = "";
            for (AnnotationExpr annotationExpr : annotations) {
                annotationString += "\n@" + ((Name) annotationExpr.getChildNodes().get(0)).asString();
            }

            List<AnnotationExpr> annotation = annotationExprs.stream().filter(annotationExpr ->
                    ((Name) annotationExpr.getChildNodes().get(0)).asString().equals("HttpTrigger")
            ).toList();
            String requestType;
            if (annotation.get(0).getChildNodes().size() == 1) {
                requestType = "GET";
            } else {
                requestType = annotation.get(0).getChildNodes().get(1).getChildNodes().get(1).toString().replace("HttpMethod.", "");
            }
            methodValuesMap.put("ANNOTATION", annotationString);
            methodValuesMap.put("REQUEST_TYPE", requestType);
            methodValuesMap.put("RETURN_TYPE", methodDeclaration.getTypeAsString());
            methodValuesMap.put("FUNCTION_NAME", methodDeclaration.getNameAsString());
            String parameterString = "";
            for (Parameter parameter : methodDeclaration.getParameters()) {
                parameterString += parameter.getTypeAsString() + " " + parameter.getNameAsString() + ", ";
            }
            methodValuesMap.put("PARAMETER", parameterString.substring(0, parameterString.length() - 2));
            methodValuesMap.put("BODY", methodDeclaration.getBody().get().toString());
            methodsString.append(TemplateProcessor.processTemplate(methodTemplate, methodValuesMap)).append("\n");
        }

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("ANNOTATION", "");
        valuesMap.put("CLASS_NAME", name);
        valuesMap.put("FIELDS", fieldsString.toString());
        valuesMap.put("METHODS", methodsString.toString());

        generateTemplateAndSaveFile(
                "src/main/java/com/example/library/generator/templates/traditional/ClassTemplate.txt",
                valuesMap,
                Paths.get(srcFolderPath+ "/" + name + ".java")
        );
        //generateProject("Test", "test", "com.example");
    }

    public static void generateProject(String name, String artifactId, String groupId) {
        String path = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().toString();
        File rootFolder = new File(path + "/Test/" + name);
        File testFolder = new File(rootFolder.getPath() + "/src/test/java/" + groupId.replace(".", "//"));
        File srcFolder = new File(rootFolder.getPath() + "/src/main/java/" + groupId.replace(".", "//"));
        srcFolderPath = srcFolder.getPath();
        testFolder.mkdirs();
        srcFolder.mkdirs();

        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("GROUP_ID", groupId);
        valuesMap.put("ARTIFACT_ID", artifactId);

        generateTemplateAndSaveFile(
                "src/main/java/com/example/library/generator/templates/pom.txt",
                valuesMap,
                Paths.get(rootFolder.getPath() + "\\pom.xml")
        );

        valuesMap = new HashMap<>();
        valuesMap.put("PACKAGE_NAME", groupId);

        generateTemplateAndSaveFile(
                "src/main/java/com/example/library/generator/templates/traditional/MainClass.txt",
                valuesMap,
                Paths.get(srcFolder.getPath() + "\\Main.java")
        );
    }


    public static void generateTemplateAndSaveFile(String templatePath, Map<String, String> values, Path savePath) {
        String template;

        template = TemplateLoader.loadTemplate(templatePath);

        String processedTemplate = TemplateProcessor.processTemplate(template, values);

        Writer.writeStringToJavaFile(processedTemplate, savePath);
    }
}
