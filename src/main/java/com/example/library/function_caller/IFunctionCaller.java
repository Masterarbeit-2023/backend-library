package com.example.library.function_caller;

import com.example.library.util.HttpMethod;

import java.util.Map;

public interface IFunctionCaller<T> {

     T call(String functionName, T obj, HttpMethod httpMethod, Map<String, Object> parameter);
     T call(String functionName);
}
