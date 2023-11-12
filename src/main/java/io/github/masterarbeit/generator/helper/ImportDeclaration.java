package io.github.masterarbeit.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportDeclaration {

    String importString;
    ClassDeclaration classDeclaration;
}
