package com.example.library.generator.config;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnPremise {
    String ip;

    @Override
    public String toString() {
        return "OnPremise{" +
                "ip='" + ip + '\'' +
                '}';
    }
}
