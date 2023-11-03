package com.example.library.generator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class Configuration {
    Provider azure;
    Provider aws;
    Provider google;

    OnPremise on_premises;
    ProviderEnum provider;
    Infrastructure infrastructure;
    String authentication;
    Map<String, Function> functions;

    public Function getConfigurationForFunction(String functionName) {
        Function function = null;
        try {
            function = functions.get(functionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        function = new Function(provider, authentication);
        return function;
    }
}
