package io.github.masterarbeit.generator.helper.method;

import io.github.masterarbeit.util.StringUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class TimerMethodDeclaration extends MethodDeclaration {
    String cron;
    int rate;

    @Override
    public Map<String, String> getMapValuesForTemplate() {

        Map<String, String> map = new HashMap<>();

        map.put("IMPORTS", getImportsForTemplate());
        map.put("CLASS_NAME", StringUtil.capitalize(name));
        map.put("FIELDS", getFieldsForTemplate());
        map.put("NAME", name);
        map.put("SCHEDULE", name);
        map.put("BODY", getBodyForTemplate());
        return map;
    }
}
