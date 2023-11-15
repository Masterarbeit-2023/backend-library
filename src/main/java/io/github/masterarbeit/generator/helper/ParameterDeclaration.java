package io.github.masterarbeit.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ParameterDeclaration {
    List<String> annotation;
    String type;
    String name;

    public boolean isPathVariable() {
        return annotation.contains("ApiPathVariable");
    }
}
