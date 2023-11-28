package io.github.masterarbeit.generator.files;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.config.ProviderEnum;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.generator.helper.method.HttpMethodDeclaration;
import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import io.github.masterarbeit.generator.helper.method.RabbitMqMethodDeclaration;
import io.github.masterarbeit.generator.helper.method.TimerMethodDeclaration;
import io.github.masterarbeit.util.StringUtil;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerlessProjectFilesGenerator extends ProjectFileGenerator {

    public void generate(List<ProjectDeclaration> projects, Configuration configuration) {
        for (ProjectDeclaration project : projects) {
            Pair<String, String> pairs = Writer.generateProjectFolders(project.getName());
            for (ClassDeclaration clazz : project.getClassDeclarations()) {
                MethodDeclaration method = clazz.getMethods().get(0);
                String fileContent = "";
                Map<String, String> map = new HashMap<>();
                StringBuilder parameters = new StringBuilder();
                String parameterType = "";
                String tmpClass = "";
                method.setClazz(clazz);
                BlockStmt body = method.getBody();
                String bodyString = "";
                ProviderEnum provider = Main.configuration.getConfigurationForFunction(method.getName()).getProvider();

                Dependency springContextDependency = new Dependency();
                springContextDependency.setGroupId("org.springframework");
                springContextDependency.setArtifactId("spring-context");
                springContextDependency.setVersion("6.0.12");
                project.getPom().addDependency(springContextDependency);

                switch (provider) {
                    case AWS -> {
                        Dependency azureDependency = new Dependency();
                        azureDependency.setGroupId("com.amazonaws");
                        azureDependency.setArtifactId("aws-lambda-java-core");
                        azureDependency.setVersion("1.1.0");
                        project.getPom().addDependency(azureDependency);
                    }
                    case AZURE -> {
                        Dependency azureDependency = new Dependency();
                        azureDependency.setGroupId("com.microsoft.azure.functions");
                        azureDependency.setArtifactId("azure-functions-java-library");
                        azureDependency.setVersion("3.0.0");
                        project.getPom().addDependency(azureDependency);
                    }
                }

                if (method instanceof HttpMethodDeclaration) {

                    if (method.getParameters().stream().filter(value -> !value.isPathVariable()).count() > 1) {
                        throw new RuntimeException("Es darf nicht mehr als ein Parameter vorhanden sein!");
                    }

                    List<ParameterDeclaration> params = method.getParameters().stream().filter(value -> !value.isPathVariable()).toList();

                    List<ParameterDeclaration> apiParams = method.getParameters().stream().filter(ParameterDeclaration::isPathVariable).toList();

                    String paramString = "";
                    String paramTypeString = "";

                    StringBuilder dummyClassStringBuilder = new StringBuilder();

                    List<FieldDeclaration> fields = method.getClazz().getFields().stream().filter(FieldDeclaration::containsAnnotationAutowired).toList();
                    StringBuilder fieldStringInsideBody = new StringBuilder();
                    for (FieldDeclaration field : fields) {
                        fieldStringInsideBody.append(field.getType()).append(" ").append(field.getName()).append(" = applicationContext.getBean(").append(field.getType()).append(".class);\n");
                    }


                    switch (provider) {
                        case AWS -> {
                            if (params.size() + apiParams.size() > 1 || params.isEmpty() && apiParams.isEmpty()) {
                                paramTypeString = clazz.getName() + ".DummyClass";
                                dummyClassStringBuilder.append("public class DummyClass {\n");

                                for (ParameterDeclaration p : params) {
                                    dummyClassStringBuilder.append(p.getType()).append(" ").append(p.getName()).append(";\n");
                                    fieldStringInsideBody.append(p.getType()).append(" ").append(p.getName()).append(" = ").append("dummy.").append(p.getName()).append(";\n");
                                }
                                for (ParameterDeclaration p : apiParams) {
                                    dummyClassStringBuilder.append(p.getType()).append(" ").append(p.getName()).append(";\n");

                                    fieldStringInsideBody.append(p.getType()).append(" ").append(p.getName()).append(" = ").append("dummy.").append(p.getName()).append(";\n");
                                }
                                dummyClassStringBuilder.append("}");
                                paramString = "DummyClass dummy";

                                bodyString = body.toString();

                            } else if (params.size() == 1) {
                                paramTypeString = params.get(0).getType();
                                paramString = params.get(0).getType() + " " + params.get(0).getName();
                            }
                            if (method.getReturnType().equals("void")) {
                                method.setReturnType("String");
                                bodyString = StringUtil.removeLastChar(body.toString());
                                bodyString += "\n";
                                bodyString += "return \"\";\n}";
                            }

                        }
                        case AZURE -> {
                            paramTypeString = !params.isEmpty() ? params.get(0).getType() : "DummyClass";
                            dummyClassStringBuilder = new StringBuilder("public class DummyClass {}");
                            for (ParameterDeclaration p : apiParams) {
                                MethodCallExpr getOrDefaultCall = new MethodCallExpr(
                                        new MethodCallExpr(new NameExpr("request"), "getQueryParameters"),
                                        "getOrDefault",
                                        new NodeList<>(
                                                new NameExpr("\"" + p.getName() + "\""),
                                                new StringLiteralExpr("")
                                        )
                                );
                                VariableDeclarationExpr varDecl = new VariableDeclarationExpr(
                                        new VariableDeclarator(
                                                new JavaParser().parseType(p.getType()).getResult().get(),
                                                p.getName(),
                                                getOrDefaultCall
                                        )
                                );
                                BlockStmt block = new BlockStmt();
                                block.addStatement(new ExpressionStmt(varDecl));
                                for (Statement stmt : body.getStatements()) {
                                    block.addStatement(stmt);
                                }
                                body = block;
                            }
                            bodyString = body.toString();
                            if (method.getReturnType().equals("void")) {
                                bodyString = StringUtil.removeLastChar(body.toString());
                                bodyString += "\n";
                                bodyString += "return request.createResponseBuilder(HttpStatus.OK).build();\n}";
                            } else {
                                bodyString = "";
                                for (String line : body.toString().split("\n")) {
                                    if (line.contains("return")) {
                                        bodyString += "return request.createResponseBuilder(HttpStatus.OK).body(" +
                                                line.replace("return", "").replace(";", "")
                                                + ").build();\n";
                                    } else {
                                        bodyString += line + "\n";
                                    }
                                }
                            }
                        }
                    }

                    bodyString = "{\n" + fieldStringInsideBody + StringUtil.removeFirstChar(bodyString);


                    fileContent = generateHttpString(
                            method.getClazz().getName(),
                            importsToString(method.getClazz().getImports()),
                            fieldsToString(method.getClazz().getFields().stream().filter(value -> !value.containsAnnotationAutowired()).toList()),
                            method.getName(),
                            ((HttpMethodDeclaration) method).getRequestType().toString(),
                            method.getReturnType(),
                            paramString,
                            paramTypeString,
                            dummyClassStringBuilder.toString(),
                            bodyString
                    );
                }

                if (method instanceof TimerMethodDeclaration) {
                    if (provider == ProviderEnum.AWS) {
                        bodyString = StringUtil.removeFirstChar(StringUtil.removeLastChar(method.getBody().toString()));
                    } else {
                        bodyString = body.toString();
                    }
                    fileContent = generateCronString(
                            clazz.getName(),
                            importsToString(clazz.getImports()),
                            fieldsToString(clazz.getFields()),
                            ((TimerMethodDeclaration) method).getCron(),
                            bodyString
                    );
                }
                if (method instanceof RabbitMqMethodDeclaration) {
                    map.put("CONNECTION_STRING_SETTING", "");
                    map.put("QUEUE", ((RabbitMqMethodDeclaration) method).getTopicName());
                    map.put("SCHEDULE", ((RabbitMqMethodDeclaration) method).getMessage());
                    if (provider == ProviderEnum.AWS) {
                        bodyString = StringUtil.removeFirstChar(StringUtil.removeLastChar(method.getBody().toString()));
                    } else {
                        bodyString = body.toString();
                    }
                    fileContent = generateRabbitMqString(
                            clazz.getName(),
                            importsToString(clazz.getImports()),
                            fieldsToString(clazz.getFields()),
                            "EXCHANGE_NAME",
                            ((RabbitMqMethodDeclaration) method).getTopicName(),
                            "CONNECTION_STRING",
                            bodyString
                    );
                }
                Pair<RequestType, Annotation> pair = Main.project.getRequestTypeAndAnnotationByMethodName(clazz.getName());
                if (!tmpClass.isBlank()) {
                    parameterType = "TmpClass";
                    parameters = new StringBuilder(parameterType + " " + StringUtil.firstCharToLowercase(parameterType));
                }
                map.put("IMPORTS", importsToString(clazz.getImports()));
                map.put("CLASS_NAME", clazz.getName());
                map.put("FIELDS", fieldsToString(clazz.getFields()));
                map.put("NAME", StringUtil.firstCharToLowercase(clazz.getName()));
                map.put("PRIVATE_CLASS", tmpClass);
                map.put("PARAMETER", parameters.toString());
                map.put("PARAMETER_TYPE", parameterType);
                map.put("BODY", body.toString());

                Writer.writeStringToFile(
                        fileContent,
                        Paths.get(pairs.getSecond() + "/" + clazz.getName() + ".java")
                );

                generateConfiguration(project.getOtherClasses(), pairs.getSecond());

                /*
                Writer.generateServerlessTemplateAndSaveFile(
                        configuration.getProvider(),
                        method.getRequestTypeAndAnnotation().getFirst(),
                        map,
                        Paths.get(pairs.getSecond() + "/" + StringUtil.capitalize(project.getName()) + ".java")
                );
                */
            }

            generateOtherClasses(project.getOtherClasses(), pairs.getSecond());
            Writer.writePomXml(pairs.getFirst() + File.separator + "pom.xml", project.getPom());
        }
    }

    private String getFunctionProvider(String functionName) {
        return Main.configuration.getConfigurationForFunction(functionName).getProvider().toString().toLowerCase();
    }

    protected String generateHttpString(String className, String imports, String fields, String functionName, String requestType, String returnType, String params, String parameterType, String dummyClass, String body) {
        Map<String, String> values = new HashMap<>();
        values.put("CLASS_NAME", className);
        values.put("IMPORTS", imports);
        values.put("FIELDS", fields);
        values.put("FUNCTION_NAME", functionName);
        values.put("REQUEST_TYPE", requestType);
        values.put("PARAMETER_TYPE", parameterType);
        values.put("RETURN_TYPE", returnType);
        values.put("PARAMETER", params);
        values.put("DUMMY_CLASS", dummyClass);
        values.put("BODY", body);

        return generateFileContent("templates/serverless/" + getFunctionProvider(functionName) + "/HttpClassTemplate.txt", values);
    }

    protected String generateCronString(String className, String imports, String fields, String cronString, String body) {
        Map<String, String> values = new HashMap<>();
        values.put("CLASS_NAME", className);
        values.put("IMPORTS", imports);
        values.put("FIELDS", fields);
        values.put("FUNCTION_NAME", StringUtil.firstCharToLowercase(className));
        values.put("CRON_STRING", cronString);
        values.put("BODY", body);

        return generateFileContent("templates/serverless/" + getFunctionProvider(StringUtil.firstCharToLowercase(className)) + "/CronClassTemplate.txt", values);
    }

    protected String generateRabbitMqString(String className, String imports, String fields, String exchangeName, String queue, String connectionString, String body) {
        Map<String, String> values = new HashMap<>();
        values.put("CLASS_NAME", className);
        values.put("IMPORTS", imports);
        values.put("FIELDS", fields);
        values.put("FUNCTION_NAME", StringUtil.firstCharToLowercase(className));
        values.put("QUEUE", queue);
        values.put("EXCHANGE_NAME", exchangeName);
        values.put("CONNECTION_STRING_SETTING", connectionString);
        values.put("BODY", body);

        return generateFileContent("templates/serverless/" + getFunctionProvider(StringUtil.firstCharToLowercase(className)) + "/EventClassTemplate.txt", values);
    }

    private void generateConfiguration(List<OtherClass> otherClasses, String packagePath) {
        String packageString = "package com.example;\n\n";
        StringBuilder importsString =
                new StringBuilder("import org.springframework.context.annotation.Bean;\n" +
                        "import org.springframework.context.annotation.ComponentScan;\n" +
                        "import org.springframework.context.annotation.Configuration;\n");

        for (OtherClass clazz : otherClasses) {
            if (clazz.getContent().contains("Service") || clazz.getContent().contains("Component")) {
                importsString.append("import ").append(clazz.getPackageName().replace(Main.configuration.getBase_package(), "com.example")).append(".").append(clazz.getClassName()).append(";\n");
            }
        }

        String annotationsString = "@Configuration\n@ComponentScan(basePackages = \"com.example\")\n";
        String clazzName = "public class SpringFunctionConfiguration {\n";

        StringBuilder beans = new StringBuilder();

        for (OtherClass clazz : otherClasses) {
            beans.append("@Bean\n");
            beans.append("public ").append(clazz.getClassName()).append(" ").append(StringUtil.firstCharToLowercase(clazz.getClassName())).append("() {\n");
            beans.append("return new ").append(clazz.getClassName()).append("();\n}");
        }


        StringBuilder clazz = new StringBuilder();
        clazz.append(packageString);
        clazz.append(importsString);
        clazz.append(annotationsString);
        clazz.append(clazzName);
        clazz.append(beans);
        clazz.append("}");

        Writer.writeStringToFile(clazz.toString(), Path.of(packagePath + "/SpringFunctionConfiguration.java"));
    }
}
