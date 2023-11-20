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
                    BlockStmt body = clazz.getMethods().get(0).getBody();
                    if (method instanceof HttpMethodDeclaration) {
                        map.put("HTTP_METHOD", ((HttpMethodDeclaration) method).getRequestType().toString());
                        map.put("RETURN_TYPE", method.getReturnType());
                        List<ParameterDeclaration> paramDecl = method.getParameters().stream().filter(value -> !value.isPathVariable()).toList();
                        if (paramDecl.size() == 1) {
                            parameterType = paramDecl.get(0).getType();
                        }
                        for (ParameterDeclaration parameterDecl : method.getParameters()) {
                            if (!parameterDecl.getAnnotation().contains("ApiPathVariable")) {
                                if (!parameters.toString().isBlank()) {
                                    parameters.append(", ");
                                }
                                parameters.append(parameterDecl.getType()).append(" ").append(parameterDecl.getName());
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
                                    // TODO Create Type
                                }
                            }
                        }
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
                    map.put("IMPORTS", importsToString(clazz.getImports()));
                    map.put("CLASS_NAME", clazz.getName());
                    map.put("FIELDS", fieldsToString(clazz.getFields()));
                    map.put("NAME", clazz.getName());
                    map.put("PARAMETER", parameters.toString());
                    map.put("PARAMETER_TYPE", parameterType);
                    map.put("BODY", body.toString());
                    Writer.generateServerlessTemplateAndSaveFile(
                            configuration.getProvider(),
                            pair.getFirst(),
                            map,
                            Paths.get(pairs.getSecond() + "/" + project.getName() + ".java")
                    );
                }
            }
        }
    }
}
