package io.github.masterarbeit.generator.files;

import com.github.javaparser.ast.stmt.BlockStmt;
import io.github.masterarbeit.generator.TemplateProcessor;
import io.github.masterarbeit.generator.config.Configuration;
import io.github.masterarbeit.generator.helper.*;
import io.github.masterarbeit.generator.helper.method.HttpMethodDeclaration;
import io.github.masterarbeit.generator.helper.method.MethodDeclaration;
import io.github.masterarbeit.generator.helper.method.RabbitMqMethodDeclaration;
import io.github.masterarbeit.generator.helper.method.TimerMethodDeclaration;
import io.github.masterarbeit.util.StringUtil;
import org.apache.maven.model.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MicroserviceProjectFileGenerator extends ProjectFileGenerator {

    public void generate(List<ProjectDeclaration> projects, Configuration configuration) {
        for (ProjectDeclaration project : projects) {
            Pair<String, String> pairs = Writer.generateProjectFolders(project.getName());
            for (ClassDeclaration clazz : project.getClassDeclarations()) {
                String fileContent = "";
                MethodDeclaration method = clazz.getMethods().get(0);
                Map<String, String> map = new HashMap<>();
                StringBuilder parameters = new StringBuilder();
                String parameterType = "";
                BlockStmt body = clazz.getMethods().get(0).getBody();
                if (method instanceof HttpMethodDeclaration) {
                    map.put("HTTP_METHOD", ((HttpMethodDeclaration) method).getRequestType().toString());
                    map.put("RETURN_TYPE", method.getReturnType());

                    for (ParameterDeclaration parameterDecl : method.getParameters()) {
                        if (!parameters.toString().isBlank()) {
                            parameters.append(", ");
                        }
                        if (!parameterDecl.getAnnotation().contains("ApiPathVariable")) {
                            parameters.append("@RequestBody ").append(parameterDecl.getType()).append(" ").append(parameterDecl.getName());
                        } else {
                            parameters.append("@RequestParam ").append(parameterDecl.getType()).append(" ").append(parameterDecl.getName());
                        }
                    }
                    String functionName = StringUtil.firstCharToLowercase(clazz.getName());
                    fileContent = generateHttpString(clazz.getName(), importsToString(clazz.getImports()), fieldsToString(clazz.getFields()), functionName, ((HttpMethodDeclaration) method).getRequestType().toString(), method.getReturnType(), parameters.toString(), method.getBody().toString());
                }
                if (method instanceof TimerMethodDeclaration) {
                    map.put("SCHEDULE", ((TimerMethodDeclaration) method).getCron());
                    map.put("RATE", "" + ((TimerMethodDeclaration) method).getRate());

                    String scheduleType = ((TimerMethodDeclaration) method).getCron() != null ? "cron" : "fixedRate";
                    String cronString = ((TimerMethodDeclaration) method).getCron() != null ? ((TimerMethodDeclaration) method).getCron() : "" + ((TimerMethodDeclaration) method).getRate();
                    fileContent = generateCronString(clazz.getName(), importsToString(clazz.getImports()), fieldsToString(clazz.getFields()), scheduleType, cronString, method.getBody().toString());
                }
                if (method instanceof RabbitMqMethodDeclaration) {
                    map.put("CONNECTION_STRING_SETTING", "");
                    map.put("QUEUE", ((RabbitMqMethodDeclaration) method).getTopicName());
                    map.put("SCHEDULE", ((RabbitMqMethodDeclaration) method).getMessage());
                    fileContent = generateRabbitMqString(clazz.getName(), importsToString(clazz.getImports()), fieldsToString(clazz.getFields()), "", "", method.getBody().toString());
                }
                map.put("IMPORTS", importsToString(clazz.getImports()));
                map.put("CLASS_NAME", clazz.getName());
                map.put("FIELDS", fieldsToString(clazz.getFields()));
                map.put("NAME", clazz.getName());
                map.put("PARAMETER", parameters.toString());
                map.put("PARAMETER_TYPE", parameterType);
                map.put("BODY", body.toString());
                Writer.writeStringToFile(
                        fileContent,
                        Paths.get(pairs.getSecond() + "/" + clazz.getName() + ".java")
                );
            }

            generateOtherClasses(project.getOtherClasses(), pairs.getSecond());

            creatMainClass(pairs.getSecond() + "/MainApplication.java");
            createPropertiesFiles(project.getProperties(), pairs.getFirst() + "/src/main/resources");

            Dependency springBootStarterDependency = new Dependency();
            springBootStarterDependency.setGroupId("org.springframework.boot");
            springBootStarterDependency.setArtifactId("spring-boot-starter");
            springBootStarterDependency.setVersion("2.7.0");

            Model pom = project.getPom();
            pom.addDependency(springBootStarterDependency);

            Plugin shadePlugin = new Plugin();
            shadePlugin.setGroupId("org.apache.maven.plugins");
            shadePlugin.setArtifactId("maven-shade-plugin");
            shadePlugin.setVersion("3.2.4"); // specify the version you need

            // Plugin configuration (if needed)
            PluginExecution pluginExecution = new PluginExecution();
            pluginExecution.setPhase("package");
            pluginExecution.addGoal("shade");
            shadePlugin.addExecution(pluginExecution);

            // Add plugin to the build
            Build build = pom.getBuild();
            if (build == null) {
                build = new Build();
                pom.setBuild(build);
            }
            build.addPlugin(shadePlugin);

            Writer.writePomXml(pairs.getFirst() + File.separator + "pom.xml", pom);
        }
    }

    private String generateHttpString(String className, String imports, String fields, String functionName, String requestType, String returnType, String params, String body) {
        Map<String, String> values = new HashMap<>();
        values.put("CLASS_NAME", className);
        values.put("IMPORTS", imports);
        values.put("FIELDS", fields);
        values.put("FUNCTION_NAME", functionName);
        values.put("REQUEST_TYPE", requestType);
        values.put("RETURN_TYPE", returnType);
        values.put("PARAMETER", params);
        values.put("BODY", body);

        return generateFileContent("templates/traditional/HttpClassTemplate.txt", values);
    }

    private String generateCronString(String className, String imports, String fields, String scheduleType, String cronString, String body) {
        Map<String, String> values = new HashMap<>();
        values.put("CLASS_NAME", className);
        values.put("IMPORTS", imports);
        values.put("FIELDS", fields);
        values.put("FUNCTION_NAME", StringUtil.firstCharToLowercase(className));
        values.put("SCHEDULE_TYPE", scheduleType);
        values.put("CRON_STRING", cronString);
        values.put("BODY", body);

        return generateFileContent("templates/traditional/CronClassTemplate.txt", values);
    }

    private String generateRabbitMqString(String className, String imports, String fields, String exchangeName, String parameter, String body) {
        Map<String, String> values = new HashMap<>();
        values.put("CLASS_NAME", className);
        values.put("IMPORTS", imports);
        values.put("FIELDS", fields);
        values.put("FUNCTION_NAME", StringUtil.firstCharToLowercase(className));
        values.put("PARAMETER", parameter);
        values.put("EXCHANGE_NAME", exchangeName);
        values.put("BODY", body);

        return generateFileContent("templates/traditional/EventClassTemplate.txt", values);
    }

    private String generateFileContent(String path, Map<String, String> values) {
        Resource resource = new ClassPathResource(path);


        String template = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            template = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return TemplateProcessor.processTemplate(template, values);
    }

    private void creatMainClass(String path) {
        Resource resource = new ClassPathResource("templates/traditional/MainClassTemplate.txt");


        String template = null;
        try {
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            template = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Writer.writeStringToFile(
                TemplateProcessor.processTemplate(template, new HashMap<String, String>()),
                Paths.get(path)
        );
    }

    private void generateDockerfile() {

    }

    private void generateDockerCompose() {

    }
}
