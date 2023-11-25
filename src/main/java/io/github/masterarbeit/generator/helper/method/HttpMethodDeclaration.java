package io.github.masterarbeit.generator.helper.method;

import io.github.masterarbeit.generator.helper.ParameterDeclaration;
import io.github.masterarbeit.util.HttpMethod;
import io.github.masterarbeit.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
public class HttpMethodDeclaration extends MethodDeclaration {
    HttpMethod requestType;
    List<String> pathVariable;
    String authentication;

    @Override
    public Map<String, String> getMapValuesForTemplate() {
        Map<String, String> map = new HashMap<>();

        map.put("IMPORTS", getImportsForTemplate());
        map.put("CLASS_NAME", StringUtil.capitalize(name));
        map.put("NAME", name);
        map.put("PARAMETER_TYPE", getParameterTypeForTemplate());
        map.put("RETURN_TYPE", getReturnType());
        map.put("PRIVATE_CLASS", getPrivateClassForTemplate());
        map.put("FIELDS", getFieldsForTemplate());
        map.put("PARAMETER", getParametersForTemplate());
        map.put("BODY", getBodyForTemplate());
        map.put("HTTP_METHOD", getRequestType().toString());

        return map;
    }

    public String getParameterTypeForTemplate() {
        switch (getProvider()) {
            case AWS -> {
                if (getParameters().size() == 1) {
                    return getParameters().get(0).getType();
                }
            }
            case AZURE, GOOGLE -> {
                List<ParameterDeclaration> paramDecl = getParameters().stream().filter(value -> !value.isPathVariable()).toList();
                if (paramDecl.size() == 1) {
                    return paramDecl.get(0).getType();
                }
            }
        }
        return "TmpClass";
    }

    public String getPrivateClassForTemplate() {
        return "";
    }

    public String getParametersForTemplate() {
        return "";
    }

    @Override
    protected String getBodyForTemplate() {
        return super.getBodyForTemplate();
    }
}
