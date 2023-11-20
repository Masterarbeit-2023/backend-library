package io.github.masterarbeit.generator.files;

import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.Configuration;
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
                    map.put("BODY", clazz.getMethods().get(0).getBody().toString());
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
