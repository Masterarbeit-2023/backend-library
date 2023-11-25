package io.github.masterarbeit.generator.files;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
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

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerlessProjectFilesGenerator extends ProjectFileGenerator {

    public void generate(List<ProjectDeclaration> projects, Configuration configuration) {
        for (ProjectDeclaration project : projects) {
            Pair<String, String> pairs = Writer.generateProjectFolders(project.getName());
            for (ClassDeclaration clazz : project.getClassDeclarations()) {
                if (clazz.isOtherClass()) {
                    generateOtherClass(clazz, pairs.getSecond());
                } else {
                    MethodDeclaration method = clazz.getMethods().get(0);
                    Map<String, String> map = new HashMap<>();
                    StringBuilder parameters = new StringBuilder();
                    String parameterType = "";
                    String tmpClass = "";
                    method.setClazz(clazz);
                    BlockStmt body = clazz.getMethods().get(0).getBody();
                    if (method instanceof HttpMethodDeclaration) {
                        map.put("HTTP_METHOD", ((HttpMethodDeclaration) method).getRequestType().toString());
                        map.put("RETURN_TYPE", method.getReturnType());
                        List<ParameterDeclaration> paramDecl = method.getParameters().stream().filter(value -> !value.isPathVariable()).toList();
                        if (paramDecl.size() == 1) {
                            parameterType = paramDecl.get(0).getType();
                        }
                        StringBuilder parameterTypes = new StringBuilder();
                        for (ParameterDeclaration parameterDecl : method.getParameters()) {
                            if (!parameterDecl.getAnnotation().contains("ApiPathVariable")) {
                                if (!parameters.toString().isBlank()) {
                                    parameters.append(", ");
                                }
                                parameters.append(parameterDecl.getType()).append(" ").append(parameterDecl.getName());
                                if (configuration.getProvider().equals(ProviderEnum.AWS) && method.getParameters().size() >= 2) {
                                    FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(new NameExpr("tmpClass"), parameterDecl.getName());
                                    VariableDeclarationExpr varDecl = new VariableDeclarationExpr(
                                            new VariableDeclarator(
                                                    new JavaParser().parseType(parameterDecl.getType()).getResult().get(),
                                                    parameterDecl.getName(),
                                                    fieldAccessExpr
                                            )
                                    );
                                    BlockStmt block = new BlockStmt();
                                    block.addStatement(new ExpressionStmt(varDecl));
                                    for (Statement stmt : body.getStatements()) {
                                        block.addStatement(stmt);
                                    }
                                    body = block;
                                }
                            } else {
                                if (configuration.getProvider().equals(ProviderEnum.AZURE)) {
                                    MethodCallExpr getOrDefaultCall = new MethodCallExpr(
                                            new MethodCallExpr(new NameExpr("request"), "getQueryParameters"),
                                            "getOrDefault",
                                            new NodeList<>(
                                                    new NameExpr(parameterDecl.getName()),
                                                    new StringLiteralExpr("")
                                            )
                                    );
                                    VariableDeclarationExpr varDecl = new VariableDeclarationExpr(
                                            new VariableDeclarator(
                                                    new JavaParser().parseType(parameterDecl.getType()).getResult().get(),
                                                    parameterDecl.getName(),
                                                    getOrDefaultCall
                                            )
                                    );
                                    BlockStmt block = new BlockStmt();
                                    block.addStatement(new ExpressionStmt(varDecl));
                                    for (Statement stmt : body.getStatements()) {
                                        block.addStatement(stmt);
                                    }
                                    body = block;
                                } else if (configuration.getProvider().equals(ProviderEnum.AWS)) {
                                    parameterTypes.append(parameterDecl.getType()).append(" ").append(parameterDecl.getName()).append(";\n");
                                    FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(new NameExpr("tmpClass"), parameterDecl.getName());
                                    VariableDeclarationExpr varDecl = new VariableDeclarationExpr(
                                            new VariableDeclarator(
                                                    new JavaParser().parseType(parameterDecl.getType()).getResult().get(),
                                                    parameterDecl.getName(),
                                                    fieldAccessExpr
                                            )
                                    );
                                    BlockStmt block = new BlockStmt();
                                    block.addStatement(new ExpressionStmt(varDecl));
                                    for (Statement stmt : body.getStatements()) {
                                        block.addStatement(stmt);
                                    }
                                    body = block;
                                }
                            }
                        }
                        tmpClass = "private class TmpClass {\n" + parameterTypes + parameterType + " " + StringUtil.firstCharToLowercase(parameterType) + ";\n}";
                    }
                    if (method instanceof TimerMethodDeclaration) {
                        map.put("SCHEDULE", ((TimerMethodDeclaration) method).getCron());
                        map.put("RATE", "" + ((TimerMethodDeclaration) method).getRate());
                    }
                    if (method instanceof RabbitMqMethodDeclaration) {
                        map.put("CONNECTION_STRING_SETTING", "");
                        map.put("QUEUE", ((RabbitMqMethodDeclaration) method).getTopicName());
                        map.put("SCHEDULE", ((RabbitMqMethodDeclaration) method).getMessage());
                    }
                    Pair<RequestType, Annotation> pair = Main.project.getRequestTypeAndAnnotationByMethodName(clazz.getName());
                    if (!tmpClass.isBlank()) {
                        parameterType = "TmpClass";
                        parameters = new StringBuilder(parameterType + " " + StringUtil.firstCharToLowercase(parameterType));
                    }
                    map.put("IMPORTS", importsToString(clazz.getImports()));
                    map.put("CLASS_NAME", clazz.getName());
                    map.put("FIELDS", fieldsToString(clazz.getFields()));
                    map.put("NAME", clazz.getName());
                    map.put("PRIVATE_CLASS", tmpClass);
                    map.put("PARAMETER", parameters.toString());
                    map.put("PARAMETER_TYPE", parameterType);
                    map.put("BODY", body.toString());

                    Writer.generateServerlessTemplateAndSaveFile(
                            configuration.getProvider(),
                            method.getRequestTypeAndAnnotation().getFirst(),
                            map,
                            Paths.get(pairs.getSecond() + "/" + StringUtil.capitalize(project.getName()) + ".java")
                    );
                }
            }

            generateOtherClasses(project.getOtherClasses(), pairs.getSecond());
            Writer.writePomXml(pairs.getFirst() + File.separator + "pom.xml", project.getPom());
        }
    }
}
