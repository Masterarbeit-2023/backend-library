package com.example.library.generator.config;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    Provider azure;
    Provider aws;
    Provider google;

    OnPremise on_premises;
    String project_config;
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
            function = new Function(provider, authentication);
        }

        return function;
    }

    public boolean isProjectConfig() {
        return project_config.equals("yes");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Configuration){
            Configuration config = (Configuration) obj;
            if (!this.azure.toString().equals(config.azure.toString())){
                return false;
            }
            if (!this.aws.toString().equals(config.aws.toString())){
                return false;
            }
            if (!this.google.toString().equals(config.google.toString())){
                return false;
            }
            if (!this.on_premises.toString().equals(config.on_premises.toString())){
                return false;
            }
            if (!this.project_config.equals(config.project_config)){
                return false;
            }
            if (!this.provider.toString().equals(config.provider.toString())){
                return false;
            }
            if (!this.infrastructure.equals(config.infrastructure)){
                return false;
            }
            if (!this.authentication.equals(config.authentication)){
                return false;
            }
            if (this.functions.size() != config.functions.size()){
                return false;
            } else {
                try {
                    for(String s : functions.keySet()) {
                        if (!functions.get(s).toString().equals(config.functions.get(s).toString())) {
                            return false;
                        }
                    }
                } catch (Exception e){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
