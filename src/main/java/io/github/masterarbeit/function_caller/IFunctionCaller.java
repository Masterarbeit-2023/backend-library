package io.github.masterarbeit.function_caller;

import io.github.masterarbeit.util.HttpMethod;

import java.util.Map;

public interface IFunctionCaller<T> {

    T call(String functionName, T obj, HttpMethod httpMethod, Map<String, Object> parameter);

    T call(String functionName);
}
