package io.github.masterarbeit.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {
    public static List combineWithoutDuplicates(List l1, List l2) {
        Set set = new LinkedHashSet<>(l1);
        set.addAll(l2);
        return new ArrayList<>(set);
    }
}
