package com.example.library.generator.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Provider {
    String key;

    @Override
    public String toString() {
        return "Provider{" +
                "key='" + key + '\'' +
                '}';
    }
}
