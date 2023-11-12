package io.github.masterarbeit.generator.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<T, G> {
    T first;
    G second;
}
