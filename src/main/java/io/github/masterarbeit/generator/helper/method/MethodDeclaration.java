package io.github.masterarbeit.generator.helper.method;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import io.github.masterarbeit.Main;
import io.github.masterarbeit.generator.config.ProviderEnum;
import io.github.masterarbeit.generator.helper.*;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class MethodDeclaration {
    List<Annotation> annotations;
    String name;
    String returnType;
    List<ParameterDeclaration> parameters;
    BlockStmt body;
    ClassDeclaration clazz;

    public boolean containsAnnotation(String annotationName) {
        if (getAnnotations().stream().filter(annotation -> annotation.getName().equals(annotationName)).count() > 0) {
            return true;
        }
        return false;
    }

    public Annotation getAnnotation(String annotationName) {
        Optional<Annotation> optionalAnnotation = getAnnotations().stream().filter(annotation -> annotation.getName().equals(annotationName)).findFirst();
        return optionalAnnotation.orElse(null);
    }


    public boolean containsAnnotationApiFunction() {
        return containsAnnotation("ApiFunction");
    }

    public Pair<RequestType, Annotation> getRequestTypeAndAnnotation() {
        for (Annotation annotation : annotations) {
            RequestType requestType = annotation.getReturnType();
            if (requestType != null) {
                return new Pair<>(requestType, annotation);
            }
        }
        return null;
    }

    public boolean bodyContainsString(String value) {
        for (Node node : body.getStatements()) {
            if (node.toString().contains(value)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getMapValuesForTemplate() {
        return new HashMap<>();
    }

    protected String getImportsForTemplate() {
        return importsToString(clazz.getImports());
    }

    protected String getFieldsForTemplate() {
        return fieldsToString(clazz.getFields());
    }

    private String importsToString(List<String> imports) {
        StringBuilder tmpString = new StringBuilder();

        for (String s : imports) {
            tmpString.append("import ").append(s).append(";\n");
        }
        return tmpString.toString();
    }

    private String fieldsToString(List<FieldDeclaration> fields) {

        StringBuilder tmpString = new StringBuilder();

        for (FieldDeclaration field : fields) {
            tmpString.append(annotationsToString(field.getAnnotations()));
            tmpString.append(field.getType()).append(" ").append(field.getName()).append(";\n");
        }
        return tmpString.toString();
    }

    private String annotationsToString(List<String> annotations) {
        StringBuilder tmpString = new StringBuilder();

        for (String s : annotations) {
            tmpString.append("@").append(s).append("\n");
        }
        return tmpString.toString();
    }

    protected String getBodyForTemplate() {
        return body.toString();
    }

    protected ProviderEnum getProvider() {
        return Main.configuration.getConfigurationForFunction(name).getProvider();
    }

}
