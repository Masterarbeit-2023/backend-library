package com.example.library.generator;

import com.example.library.Main;
import com.example.library.generator.config.Configuration;
import com.example.library.generator.helper.*;
import org.antlr.v4.runtime.misc.Pair;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectFileGenerator {

    public static void generateProjectServerless(List<ProjectDeclaration> projects, Configuration configuration) {
        for (ProjectDeclaration project : projects) {
            Pair<String, String> pairs = Writer.generateProjectFolders(project.getName());
            for (ClassDeclaration clazz : project.getClassDeclarations()) {
                Pair<RequestType, Annotation> pair = Main.project.getRequestTypeAndAnnotationByMethodName(clazz.getName());
                Map<String, String> map = new HashMap<>();
                map.put("IMPORTS", "");
                map.put("CLASS_NAME", clazz.getName());
                map.put("FIELDS", "");
                map.put("METHODS", "");
                map.put("NAME", clazz.getName());
                String httpMethod = pair.b.getValues().get("httpMethod") ;
                if (pair.b.getValues().get("httpMethod") != null){
                    map.put("HTTP_METHOD", httpMethod.replace("HhttpMethod.", "").toLowerCase());
                } else {
                    map.put("HTTP_METHOD", "");
                }

                map.put("PARAMETER", clazz.getMethods().get(0).getParameters().toString());
                map.put("PARAMETER_TYPE", clazz.getMethods().get(0).getParameters().toString());
                map.put("BODY", clazz.getMethods().get(0).getBody().toString());
                Writer.generateServerlessTemplateAndSaveFile(
                        configuration.getProvider(),
                        pair.a,
                        map,
                        Paths.get(pairs.b+ "/" + project.getName() + ".java")
                );
            }
        }
    }

}
