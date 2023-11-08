package com.example.library.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<T, G> {
    T first;
    G second;
}
