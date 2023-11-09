package com.example.library.generator.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OnPremise {
    String ip;

    @Override
    public String toString() {
        return "OnPremise{" +
                "ip='" + ip + '\'' +
                '}';
    }
}
