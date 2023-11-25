package io.github.masterarbeit.generator.helper.method;

import io.github.masterarbeit.util.StringUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RabbitMqMethodDeclaration extends MethodDeclaration {
    String topicName;
    String message;

    @Override
    public Map<String, String> getMapValuesForTemplate() {

        Map<String, String> map = new HashMap<>();

        map.put("IMPORTS", getImportsForTemplate());
        map.put("CLASS_NAME", StringUtil.capitalize(name));
        map.put("FIELDS", getFieldsForTemplate());
        map.put("NAME", name);
        map.put("CONNECTION_STRING_SETTING", name);
        map.put("QUEUE", topicName);
        map.put("BODY", getBodyForTemplate());
        return map;
    }
}
