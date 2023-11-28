package io.github.masterarbeit.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FieldDeclaration {

    String name;
    List<String> annotations;
    String type;

    public boolean containsAnnotation(String annotation) {
        for (String s : annotations) {
            if (s.equals(annotation)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAnnotationAutowired() {
        return containsAnnotation("Autowired");
    }
}
