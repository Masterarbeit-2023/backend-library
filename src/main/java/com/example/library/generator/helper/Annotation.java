package com.example.library.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Annotation {
    String name;
    Map<String, String> values;

    public RequestType getReturnType() {
        switch (name) {
            case "HttpTrigger"-> {
                return RequestType.Http;
            }
            case "DatabaseTrigger"->{
                return RequestType.Database;
            }
            case "TimerTrigger"-> {
                return RequestType.Timer;
            }
            case "RabbitMqTrigger"->{
                return RequestType.RabbitMq;
            }
            default -> {
                return null;
            }
        }
    }
}
