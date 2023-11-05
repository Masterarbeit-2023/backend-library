package com.example.library.generator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateProcessor {

    public static String processTemplate(String template, Map<String, String> valuesMap) {
        Pattern pattern = Pattern.compile("\\{\\{(.+?)\\}\\}");
        Matcher matcher = pattern.matcher(template);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = valuesMap.get(matcher.group(1));
            if (replacement == null) throw new IllegalStateException("Placeholder not found in values map. " + matcher.group(1));
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

}
