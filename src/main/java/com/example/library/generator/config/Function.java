package com.example.library.generator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
