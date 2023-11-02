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
}
