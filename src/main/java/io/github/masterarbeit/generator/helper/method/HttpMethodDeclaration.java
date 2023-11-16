package io.github.masterarbeit.generator.helper.method;

import io.github.masterarbeit.util.HttpMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
public class HttpMethodDeclaration extends MethodDeclaration {
    HttpMethod requestType;
    List<String> pathVariable;
    String authentication;
}
