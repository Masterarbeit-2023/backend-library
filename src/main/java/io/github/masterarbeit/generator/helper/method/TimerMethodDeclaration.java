package io.github.masterarbeit.generator.helper.method;

import lombok.Data;

@Data
public class TimerMethodDeclaration extends MethodDeclaration {
    String cron;
    int rate;
}
