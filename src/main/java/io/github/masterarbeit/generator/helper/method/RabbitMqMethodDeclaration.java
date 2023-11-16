package io.github.masterarbeit.generator.helper.method;

import lombok.Data;

@Data
public class RabbitMqMethodDeclaration extends MethodDeclaration {
    String topicName;
    String message;
}
