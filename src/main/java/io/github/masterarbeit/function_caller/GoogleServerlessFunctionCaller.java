package io.github.masterarbeit.function_caller;

import io.github.masterarbeit.util.HttpMethod;

import java.util.Map;

public class GoogleServerlessFunctionCaller<T> implements IFunctionCaller<T> {

    @Override
    public T call(String functionName, T obj, HttpMethod httpMethod, Map<String, Object> parameter) {
        return null;
    }

    @Override
    public T call(String functionName) {
        return null;
    }
}