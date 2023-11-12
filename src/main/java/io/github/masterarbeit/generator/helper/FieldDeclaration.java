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
}
