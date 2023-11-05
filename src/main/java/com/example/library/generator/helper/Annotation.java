package com.example.library.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Annotation {
    String name;
    Map<String, String> values;
}
