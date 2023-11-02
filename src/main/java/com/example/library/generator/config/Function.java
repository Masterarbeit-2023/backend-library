package com.example.library.generator.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Function {
    ProviderEnum provider;
    String authentication;

    @Override
    public String toString() {
        return "Function{" +
                "provider=" + provider +
                ", authentication='" + authentication + '\'' +
                '}';
    }
}
