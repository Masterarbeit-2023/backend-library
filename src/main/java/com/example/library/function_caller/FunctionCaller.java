package com.example.library.function_caller;

import com.example.library.util.HttpMethod;

import java.util.Map;

public class FunctionCaller<T> implements IFunctionCaller<T>{


    @Override
    public T call(String functionName, T obj, HttpMethod httpMethod, Map<String, Object> parameter) {
        return null;
    }

    @Override
    public T call(String functionName) {
        return null;
    }
}
